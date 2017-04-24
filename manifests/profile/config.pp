#
# Copyright (c) IN2P3 Computing Centre, IN2P3, CNRS
#
# Contributor(s) : ccin2p3
#

# == Class riemann::profile::config
#
# This profile implements a Riemann forwarder
# which is basically a Riemann server which will
# automatically forward to all hosts which
# subscribe to it
#
class riemann::profile::config (
  $config_listen = {},
  $config_fragment = {},
  $config_streams = {},
  $config_let = {},
  $config_stream = {},
) {

  if ($config_listen) {
    create_resources('::riemann::listen', $config_listen)
  }
  if ($config_fragment) {
    create_resources('::riemann::config::fragment', $config_fragment)
  }
  if ($config_streams) {
    create_resources('::riemann::streams', $config_streams)
  }
  if ($config_let) {
    create_resources('::riemann::let', $config_let)
  }
  if ($config_stream) {
    create_resources('::riemann::stream', $config_stream)
  }
}
# vim: ft=puppet
