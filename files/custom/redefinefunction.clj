(ns riemann.common)

(defn subject [events]
  (str (join " " (keep identity
              [(human-uniq (map :host events) "hosts")
               (case (human-uniq (map :service events) "services")
                        "cpu" "health-CPU"
                        "memory" "health-内存"
                        "disk /" "health-根磁盘"
                        "load" "health-负载"
                        "cdn.sys.load.1m" "主机监控-主机负载"
                        "cdn.sys.tcpconns.tcp_connections.established" "主机监控-并发连接数"
                        "cdn.sys.cpu.user" "CPU监控-单核最高利用率"
                        "cdn.sys.cpuall.user" "CPU监控-CPU利用率"
                        "cdn.sys.cpu.wait" "CPU监控-IO wait"
                        "cdn.sys.disk.disk_ops.read" "磁盘监控-读TPS"
                        "cdn.sys.disk.disk_ops.write" "磁盘监控-写TPS"
                        "cdn.sys.disk.disk_octets.read" "磁盘监控-读速率"
                        "cdn.sys.disk.disk_octets.write" "磁盘监控-写速率"
                        "cdn.sys.if.if_packets.tx" "网卡监控-出口包量"
                        "cdn.sys.if.if_packets.rx" "网卡监控-入口包量"
                        "cdn.sys.if.if_octets.tx" "网卡监控-出口流量"
                        "cdn.sys.if.if_octets.rx" "网卡监控-入口流量"
                        "cdn.sys.memory.percent.used" "内存监控-内存利用率"
                        "cdn.sys.swap.percent.used" "内存监控-交换分区（swap）利用率"
                        "flowinMin" "流量监控-流入流量"
                        "flowoutMin" "流量监控-流出流量"
                        "flowHitRatioMin" "流量监控-字节命中率"
                        "flowinRateMin" "速率监控-流入速率"
                        "flowoutRateMin" "速率监控-流出速率"
                        "throughputMin" "速率监控-节点吞吐量"
                        "gainRatioMin" "速率监控-增益比"
                        "reqMin" "业务请求监控-业务请求数"
                        "reqOkMin" "业务请求监控-请求成功数"
                        "reqHitRatioMin" "业务请求监控-请求命中率"
                        "req4CodeMin" "业务请求监控-客户端错误数"
                        "req5CodeMin" "业务请求监控-服务端错误数"
                        "serveDelayMin" "业务请求监控-首字节响应时延"
                        "reqbackMin" "回源请求监控-回源请求数"
                        "reqbackOkMin" "回源请求监控-回源成功数"
                        "back4CodeMin" "回源请求监控-回源客户端错误数"
                        "back5CodeMin" "回源请求监控-回源服务端错误数"
                        (human-uniq (map :service events) "services")
               )
               ;(human-uniq (map :state events) "states")
              ]
            )
        )
        "告警"
  )
)

(defn body [events]
  (join "\n\n\n"
        (map
          (fn [event]
            (str
              "告警时间：\t" (time-at (:time event)) "\n"
              "主机名称：\t" (:host event) "\n"
              "告警指标：\t" (case (:service event)
                        "cpu" "health-CPU"
                        "memory" "health-内存"
                        "disk /" "health-根磁盘"
                        "load" "health-负载"
                        "cdn.sys.load.1m" "主机监控-主机负载"
                        "cdn.sys.tcpconns.tcp_connections.established" "主机监控-并发连接数"
                        "cdn.sys.cpu.user" "CPU监控-单核最高利用率"
                        "cdn.sys.cpuall.user" "CPU监控-CPU利用率"
                        "cdn.sys.cpu.wait" "CPU监控-IO wait"
                        "cdn.sys.disk.disk_ops.read" "磁盘监控-读TPS"
                        "cdn.sys.disk.disk_ops.write" "磁盘监控-写TPS"
                        "cdn.sys.disk.disk_octets.read" "磁盘监控-读速率"
                        "cdn.sys.disk.disk_octets.write" "磁盘监控-写速率"
                        "cdn.sys.if.if_packets.tx" "网卡监控-出口包量"
                        "cdn.sys.if.if_packets.rx" "网卡监控-入口包量"
                        "cdn.sys.if.if_octets.tx" "网卡监控-出口流量"
                        "cdn.sys.if.if_octets.rx" "网卡监控-入口流量"
                        "cdn.sys.memory.percent.used" "内存监控-内存利用率"
                        "cdn.sys.swap.percent.used" "内存监控-交换分区（swap）利用率"
                        "flowinMin" "流量监控-流入流量"
                        "flowoutMin" "流量监控-流出流量"
                        "flowHitRatioMin" "流量监控-字节命中率"
                        "flowinRateMin" "速率监控-流入速率"
                        "flowoutRateMin" "速率监控-流出速率"
                        "throughputMin" "速率监控-节点吞吐量"
                        "gainRatioMin" "速率监控-增益比"
                        "reqMin" "业务请求监控-业务请求数"
                        "reqOkMin" "业务请求监控-请求成功数"
                        "reqHitRatioMin" "业务请求监控-请求命中率"
                        "req4CodeMin" "业务请求监控-客户端错误数"
                        "req5CodeMin" "业务请求监控-服务端错误数"
                        "serveDelayMin" "业务请求监控-首字节响应时延"
                        "reqbackMin" "回源请求监控-回源请求数"
                        "reqbackOkMin" "回源请求监控-回源成功数"
                        "back4CodeMin" "回源请求监控-回源客户端错误数"
                        "back5CodeMin" "回源请求监控-回源服务端错误数"
                        (:service event)
                                  )"\n"
              "当前数值：\t"
              (if (ratio? (:metric event))
                (double (:metric event))
                (:metric event)) "\n"
              "Tags: [" (join ", " (:tags event)) "]\n"
              "Custom Attributes: " (custom-attributes event) "\n\n"
              "详细描述：\t" (:description event)
            )
          )
          events
        )
  )
)

(ns riemann.config)
