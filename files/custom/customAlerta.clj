; -*- mode: clojure; -*-
; vim: filetype=clojure
(ns customAlerta
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
;            [riemann.query :as query]
  )
)

;(def hostname (.getHostName (java.net.InetAddress/getLocalHost)))

(defn post-to-alerta
  "POST to the Alerta REST API."
  [url request]
  (let [event-url url
        event-json (json/generate-string request)]
        (client/post event-url
               {:body event-json
                :socket-timeout 5000
                :conn-timeout 5000
                :content-type :json
                :accept :json
                ; :debug true
                ; :debug-body true
                :throw-entire-message? true}))
)

(defn format-alerta-event
  "Formats an event for Alerta."
  [event]
  {
   :alarmOccurTime (riemann.common/unix-to-iso8601-8timezone (:time event))
   :hostName (:host event)
   :ruleNum (case (:service event)
             "warnFlowinRateMin大于阀值" "000001"
             "warnFlowinRateMin小于阀值" "000002"
             "warnFlowoutRateMin大于阀值" "000003"
             "warnFlowoutRateMin小于阀值" "000004"
             "cdn.sys.tcpconns.tcp_connections.established" "000005"
             "warnReqErrorRatio" "000006"
             "cdn.sys.if.if_octets.tx" "000007"
             "cdn.sys.cpuall.user" "100001"
             "cdn.sys.memory.percent.used" "100002"
             "cdn.sys.df.percent_bytes.used系统盘" "100003"
             "cdn.sys.disk.disk_ops.read" "100004"
             "cdn.sys.disk.disk_ops.write" "100005"
             "cdn.sys.df.percent_bytes.used启动盘" "100006"
             "cdn.ping.ping_droprate.117.131.213.238" "100007"
             (:service event)
            )
   :currentValue
    (if (ratio? (:metric event))
        (double (:metric event))
        (:metric event))
   :description (:description event)
   :alarmState (:state event)
;   :tags (if (empty? (:tags event)) [] (:tags event))
;   :origin (str "riemann/" hostname)
;   :rawData event
  }
)

(defn alerta
  "Creates an alerta adapter."
  [e & {:keys [override]}]
  (let [override (merge {:alert "http://localhost:8080/alert"} override)]
    (post-to-alerta
        (:alert override)
        (format-alerta-event e)))
)
