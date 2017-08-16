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
    ;#(info ">>>>>>>>>>>>>>>>>>>>>>>>> alert-log ##" (:service %) (:state %) (:metric %) (:description %))
)

(def changed-notice
    (changed-state
        alert-notice
        #(info ">>>>>>>>>>>>>>>>>>>>>>>>> changed-log ##" (:service %) (:state %) (:metric %))
    )
   ; (changed-state {:init "2"}
   ;     (stable 30 :state
   ;        #(info ">>>>>>>>>>>>>>>>>>>>>>>>> changed-log ##" (:service %) (:state %) (:metric %))
   ;     )
   ; )
)

(streams
  (where (not (state "expired"))
    (by :host
;;;
        (match :service "cpu"
           (with :state "nil"
                #(info ">>>>>>>>>>>>>>>>>>>>>>>>> Hello Event ##" (:service %) (:state %) (:metric %))
               (fixed-time-window 60
                  #(info ">>>>>>>>>>>>>>>>>>>>>>>>> Fixed-time-log ##" %)
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (* (:metric %) 100) 80) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (:service (last events))
                                   ;:metric (:metric (apply max-key :metric events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       #(info ">>>>>>>>>>>>>>>>>>>>>>>>> New-log ##" (:service %) (:state %) (:metric %))
                       ; for state change
                       (splitp = state
                         "alarm" (alarmstate "CPU utilisation > 80%" changed-notice)
                         "normal" (normalstate "CPU utilisation is OK" changed-notice)
                       )
                       ; for alert
                       (where (state "alarm")
                         (alarmstate "CPU utilisation > 80%" alert-notice)
                       )
                  )
               )
            )
        )
;;;
;;;
        (where (and (service "cdn.sys.df.percent_bytes.used") (= (:device event) "root"))
           (with :state "nil"
                #(info ">>>>>>>>>>>>>>>>>>>>>>>>> Hello Event ##" (:service %) (:state %) (:metric %))
               (fixed-time-window 60
                  #(info ">>>>>>>>>>>>>>>>>>>>>>>>> Fixed-time-log ##" %)
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 85) events)) (count events))]
                           (event {:time (:time (last events))
                                   :host (:host (last events))
                                   :service (str (:service (last events)) "系统盘")
                                   ;:metric (:metric (apply max-key :metric events))
                                   :metric (:metric (last events))
                                   :description (:description (last events))
                                   :state   (condp < fraction
                                             0.99 "alarm"
                                                  "normal")})
                         )
                       )
                       #(info ">>>>>>>>>>>>>>>>>>>>>>>>> New-log ##" (:service %) (:state %) (:metric %))
                       ; for state change
                       (splitp = state
                         "alarm" (alarmstate "Root device utilisation > 85%" changed-notice)
                         "normal" (normalstate "Root device utilisation is OK" changed-notice)
                       )
                       ; for alert
                       (where (state "alarm")
                         (alarmstate "Root device utilisation > 85%" alert-notice)
                       )
                  )
               )
            )
        )
;;;
    )
  )
)
