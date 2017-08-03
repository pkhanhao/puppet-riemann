(streams
(where (not (state "expired"))
    (by :host
;;;
        (where (service "cdn.sys.load.1m")
            (where (>= metric 40)
               (adjust [:description str "\n\n 告警阀值-指标 >= 40 ，告警为立即触发。"]
                      (throttle 1 1800
                         ((mailer {:from "Riemann告警邮件<riemann@localhost>"}) "test@163.com" "test@126.com")
                       )
                )
            )
        )
;;;
;;;
        (where (service "cdn.sys.df.percent_bytes.used")
            (where (>= metric 75)
               (adjust [:description str "\n\n 告警阀值-指标 >= 75 ，告警为立即触发。"]
                      (throttle 1 1800
                         ((mailer {:host "smtp.chinamobile.com" :port 25 :user "riemann@cmhi.chinamobile.com" :pass "xyz" :from "告警邮件<riemann@cmhi.chinamobile.com>"}) "test@163.com")
                       )
                )
            )
        )
;;;
)))
