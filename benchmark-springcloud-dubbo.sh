#!/bin/bash

# JVM 参数配置
SERVER_OPTS="-Xms3g -Xmx3g -XX:+UseG1GC"
CLIENT_OPTS="-Xms3g -Xmx3g -XX:+UseG1GC"

# 创建日志目录
mkdir -p logs

# 清理旧日志
rm -f logs/*.log
#rm -rf benchmark-results

# 设置时间戳
TIMESTAMP=$(date '+%Y%m%d_%H%M%S')

# 创建归档目录
ARCHIVE_DIR="benchmark-archive"
mkdir -p "$ARCHIVE_DIR"

# 设置日志文件
SCRIPT_LOG="logs/benchmark.log"
SERVER_LOG="logs/server.log"
CLIENT_LOG="logs/client.log"

# 记录日志的函数
log() {
    local timestamp=$(date '+%Y-%m-%d %H:%M:%S')
    echo "[$timestamp] $1" | tee -a "$SCRIPT_LOG"
}

# 记录命令输出的函数
log_cmd() {
    local log_file=$1
    shift
    ("$@" 2>&1 | tee -a "$log_file") || return $?
}

# 检查nacos是否运行
log "检查 Nacos 服务状态..."
nc -z localhost 8848
if [ $? -ne 0 ]; then
    log "错误: Nacos 未在 8848 端口运行"
    exit 1
fi

# 检查参数
if [ $# -lt 1 ]; then
    log "用法: $0 <测试类型>"
    log "可用的测试类型:"
    log "  springcloud - 测试 Spring Cloud REST 服务"
    log "  feigndubbo - 测试 Feign 调用 Dubbo REST 服务"
    log "  dubborest  - 测试 Dubbo REST 协议"
    log "  dubbotriple - 测试 Dubbo Triple 协议"
    log "  dubborpc   - 测试 Dubbo RPC 协议"
    log "  all        - 顺序运行所有测试"
    exit 1
fi

TEST_TYPE=$1

# 编译项目
log "编译项目..."
log_cmd "$SCRIPT_LOG" mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    log "错误: 项目编译失败"
    exit 1
fi

# 检查 jar 文件是否存在
SERVER_JAR="springcloud-dubbo-server/target/springcloud-dubbo-server-1.0-SNAPSHOT.jar"
CLIENT_JAR="springcloud-dubbo-client/target/benchmark.jar"

if [ ! -f "$SERVER_JAR" ]; then
    log "错误: 找不到服务端 jar 文件: $SERVER_JAR"
    exit 1
fi

if [ ! -f "$CLIENT_JAR" ]; then
    log "错误: 找不到客户端 jar 文件: $CLIENT_JAR"
    exit 1
fi

# 启动服务端
log "启动 Dubbo 服务端..."
java $SERVER_OPTS -jar "$SERVER_JAR" > >(tee -a "$SERVER_LOG") 2>&1 &
SERVER_PID=$!

# 检查服务端是否成功启动
log "等待服务端启动..."
for i in {1..30}; do
    sleep 2
    if grep -q "Started SpringCloudDubboServerApplication" "$SERVER_LOG"; then
        log "服务端启动成功"
        break
    fi
    if [ $i -eq 30 ]; then
        log "错误: 服务端启动超时"
        kill $SERVER_PID
        exit 1
    fi
done

# 运行指定的测试任务
run_test() {
    local test_type=$1
    log "开始运行压测: $test_type"
    log "----------------------------------------"
    
    # 创建结果目录
    mkdir -p benchmark-results
    
    # 运行测试
    java $CLIENT_OPTS -jar "$CLIENT_JAR" $test_type > >(tee -a "$CLIENT_LOG") 2>&1
    if [ $? -ne 0 ]; then
        log "警告: $test_type 测试执行失败"
        return 1
    fi
    
    # 使用时间戳重命名结果文件
    local result_file="benchmark-${test_type}_${TIMESTAMP}.json"
    mv "benchmark-$test_type.json" "$result_file"
    
    # 复制一份到归档目录
    cp "$result_file" "$ARCHIVE_DIR/"
    
    log "测试完成: $test_type"
    log "当前结果文件: $result_file"
    log "已归档到: $ARCHIVE_DIR/$result_file"
    log "----------------------------------------"
}

# 清理函数
cleanup() {
    log "正在清理..."
    if [ ! -z "$SERVER_PID" ]; then
        log "关闭服务端进程 (PID: $SERVER_PID)..."
        kill $SERVER_PID 2>/dev/null
        wait $SERVER_PID 2>/dev/null
        log "服务端进程已关闭"
    else
        log "没有需要清理的服务端进程"
    fi
    log "清理完成"
}

# 注册清理函数
trap cleanup EXIT

# 运行测试
if [ "$TEST_TYPE" = "all" ]; then
    log "开始运行所有测试..."
    for type in springcloud feigndubbo dubborest dubbotriple dubborpc; do
        run_test $type
        if [ $? -ne 0 ]; then
            log "警告: $type 测试失败，继续执行下一个测试"
        fi
    done
else
    run_test $TEST_TYPE
fi

log "压测完成" 