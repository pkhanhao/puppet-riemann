#
define riemann::cmccstream (
  $mail_from = $riemann::mail_from,
  $mail_to = $riemann::mail_to,
  $host_regex = $riemann::host_regex,
  $time_length = $riemann::time_length,
  $time_granularity = $riemann::time_granularity,
  $alarm_relation = $riemann::alarm_relation,
  $alarm_conditions = $riemann::alarm_conditions,
  $streams = 'default',
)
{
  include ::riemann
  if !defined(Riemann::Streams[$streams]) {
    riemann::streams { $streams: }
  }

  @riemann::config::fragment { "cmccstream ${title}":
    section    => "cmccstreams ${streams}",
    subscriber => 'local',
    content    => template('riemann/cmccstream.erb'),
  }
}
