(streams
(where (not (state "expired"))
  (where (host #"^xy-data4.cmcdn.net$")
    (by :host
;;;
        (where (and (service "cdn.sys.processes.ps_count") (= (:instance event) "hbase"))
            (where (< metric 1)
               (adjust [:description str "\n\n 告警阀值-指标 < 1 ，告警为立即触发。"]
                      (throttle 1 1800
                         ((mailer {:host "smtp.chinamobile.com" :port 25 :user "riemann@cmhi.chinamobile.com" :pass "xyz" :from "告警邮件<riemann@cmhi.chinamobile.com>"}) "test@163.com")
                      )
               )
            )
        )
;;;
))))
