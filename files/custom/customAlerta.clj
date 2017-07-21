; -*- mode: clojure; -*-
; vim: filetype=clojure
(ns customAlerta
  (:require [clj-http.client :as client]
            [cheshire.core :as json]
;            [riemann.query :as query]
  )
)

(def hostname (.getHostName (java.net.InetAddress/getLocalHost)))

(def alerta-endpoints
        {:alert "http://127.0.0.1:8080/v1/alarm"
        :heartbeat "http://127.0.0.1:8080/heartbeat"})

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
             "cpu" "10001"
             "memory" "10002"
             "disk /" "10003"
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
  "Creates an alerta adapter. (changed-state (alerta))"
  [e]
  (post-to-alerta
        (:alert alerta-endpoints)
        (format-alerta-event e))
)

(defn heartbeat
  [e]
  (post-to-alerta
        (:heartbeat alerta-endpoints)
        {:origin (str "riemann/" hostname)
         :type "Heartbeat"})
)
