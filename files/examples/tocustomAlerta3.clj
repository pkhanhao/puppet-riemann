(require 'customAlerta)

(def alertapi "http://172.23.195.75:8011/v1/alarm")

(defn severity
    [severity message & children]
        (fn [e] ((apply with {:state severity :description message} children) e))
)

(def alarmstate (partial severity "1"))
(def normalstate (partial severity "2"))

(defn alert-notice [e]
    (customAlerta/alerta e :override {:alert alertapi})
)

(def changed-notice
    (changed-state
        alert-notice
    )
)

(streams
  (where (not (state "expired"))
    (by :host
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "warnFlowinRateMin"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (/ (:metric %) 1048576) 2048) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (str (:service (last events)) "大于阀值")
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "回源流量超出设定的阈值2G" changed-notice)
                         "normal" (normalstate "回源流量超出设定的阈值2G(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "回源流量超出设定的阈值2G" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "warnFlowinRateMin"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(< (/ (:metric %) 1048576) 1) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (str (:service (last events)) "小于阀值")
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "回源流量低于设定的阈值1M" changed-notice)
                         "normal" (normalstate "回源流量低于设定的阈值1M(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "回源流量低于设定的阈值1M" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "warnFlowoutRateMin"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (/ (:metric %) 1048576) 6144) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (str (:service (last events)) "大于阀值")
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "本地服务流量超出设定的阈值6G" changed-notice)
                         "normal" (normalstate "本地服务流量超出设定的阈值6G(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "本地服务流量超出设定的阈值6G" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "warnFlowoutRateMin"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(< (/ (:metric %) 1048576) 10) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (str (:service (last events)) "小于阀值")
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "本地服务流量低于设定的阈值10M" changed-notice)
                         "normal" (normalstate "本地服务流量低于设定的阈值10M(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "本地服务流量低于设定的阈值10M" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.sys.tcpconns.tcp_connections.established"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 20000) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "并发连接数超过设定的阈值2W" changed-notice)
                         "normal" (normalstate "并发连接数超过设定的阈值2W(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "并发连接数超过设定的阈值2W" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "warnReqErrorRatio"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 3) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "异常CODE码比例超过阈值3%" changed-notice)
                         "normal" (normalstate "异常CODE码比例超过阈值3%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "异常CODE码比例超过阈值3%" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.sys.if.if_octets.tx"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (/ (:metric %) 1048576) 7168) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "业务服务器出流网卡超过阈值7G" changed-notice)
                         "normal" (normalstate "业务服务器出流网卡超过阈值7G(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "业务服务器出流网卡超过阈值7G" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.sys.cpuall.user"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 85) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "CPU占用率超出设定的阈值85%" changed-notice)
                         "normal" (normalstate "CPU占用率超出设定的阈值85%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "CPU占用率超出设定的阈值85%" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.sys.memory.percent.used"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 85) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "内存占用率超出设定的阈值85%" changed-notice)
                         "normal" (normalstate "内存占用率超出设定的阈值85%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "内存占用率超出设定的阈值85%" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (where (and (service "cdn.sys.df.percent_bytes.used") (= (:device event) "root"))
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 85) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (str (:service (last events)) "系统盘")
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "系统磁盘使用率超过设定的阈值85%" changed-notice)
                         "normal" (normalstate "系统磁盘使用率超过设定的阈值85%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "系统磁盘使用率超过设定的阈值85%" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.sys.disk.disk_ops.read"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (/ (:metric %) 10000) 0.85) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "磁盘IO读使用率超出设定的阈值85%" changed-notice)
                         "normal" (normalstate "磁盘IO读使用率超出设定的阈值85%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "磁盘IO读使用率超出设定的阈值85%" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.sys.disk.disk_ops.write"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (/ (:metric %) 10000) 0.85) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "磁盘IO写使用率超出设定的阈值85%" changed-notice)
                         "normal" (normalstate "磁盘IO写使用率超出设定的阈值85%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "磁盘IO写使用率超出设定的阈值85%" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.sys.df.percent_bytes.used"
          (by :device
            (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 80) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "硬盘剩余空间不足20%" changed-notice)
                         "normal" (normalstate "硬盘剩余空间不足20%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "硬盘剩余空间不足20%" alert-notice)
                       )
                  )
                )
            )
          )
        )
;;;;;;;;;;;;;;;;;;;;;;;
;;;;;;;;;;;;;;;;;;;;;;;
        (match :service "cdn.ping.ping_droprate.117.131.213.238"
           (with :state "nil"
                (fixed-time-window 300
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 0.2) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       (splitp = state
                         "alarm" (alarmstate "服务器连接外网丢包率大于20%" changed-notice)
                         "normal" (normalstate "服务器连接外网丢包率大于20%(告警清除)" changed-notice)
                       )
                       (where (state "alarm")
                         (alarmstate "服务器连接外网丢包率大于20%" alert-notice)
                       )
                  )
                )
            )
        )
;;;;;;;;;;;;;;;;;;;;;;;
    )
  )
)
