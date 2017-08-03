(let [index (default :ttl 60 (index))]
  (streams
    (with :ttl 60
       index
    )
      (by :host
;;;
            (where (or (service "cdn.sys.memory.percent.used") (service "cdn.sys.cpuall.user"))
             (expired
               (adjust [:service str "[指标过期]"]
                 ;(fn [event] (info "-----> Expired" event))
                 (throttle 2 1800
                    ((mailer {:host "smtp.chinamobile.com" :port 25 :user "riemann@cmhi.chinamobile.com" :pass "xyz" :from "告警邮件<riemann@cmhi.chinamobile.com>"}) "test@163.com")
                 )
               )
             )
            )
;;;
      )
  )
)
