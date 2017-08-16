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
        (match :service "cpu"
           (with :state "nil"
                #(info ">>>>>>>>>>>>>>>>>>>>>>>>> Hello Event ##" (:service %) (:state %) (:metric %))
                ; for state change
                (splitp < (* metric 100)
                 80 (alarmstate "CPU utilisation > 80%" changed-notice)
                    (normalstate "CPU utilisation is OK" changed-notice)
                )
                ; for alert
                (where (> metric 0.8)
                    (alarmstate "CPU utilisation > 80%" alert-notice)
                )
            )
        )
    )
  )
)
