(require 'customAlerta)

(defn severity
    [severity message & children]
        (fn [e] ((apply with {:state severity :description message} children) e))
)

(def alarmstate (partial severity "1"))
(def normalstate (partial severity "2"))

(def alert-notice
    ;hhAlerta/alerta
    #(info ">>>>>>>>>>>>>>>>>>>>>>>>> alert-log ##" (:service %) (:state %) (:metric %) (:description %))
)

(def changed-notice
    (changed-state {:init "2"}
        ;hhAlerta/alerta
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
        (match :service "cpu"
           (with :state "nil"
                #(info ">>>>>>>>>>>>>>>>>>>>>>>>> Hello Event ##" (:service %) (:state %) (:metric %))
               (fixed-time-window 60
                  ;#(info ">>>>>>>>>>>>>>>>>>>>>>>>> Fixed-time-log ##" %)
                  (smap
                       (fn [events]
                         (let [fraction (/ (count (filter #(> (:metric %) 0.8) events)) (count events))]
                           (event {:time (:time (first events))
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
    )
  )
)
