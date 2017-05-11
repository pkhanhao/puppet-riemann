#
# Copyright (c) IN2P3 Computing Centre, IN2P3, CNRS
#
# Contributor(s) : ccin2p3
#

# == Class riemann::params
#
# This class is called from riemann
# It sets platform specific defaults
#
class riemann::params {
  case $::osfamily {
    'Debian': {
      $package_name = 'riemann'
      $service_name = 'riemann'
      $config_dir = '/etc/riemann'
      $init_config_file = '/etc/default/riemann'
      $reload_command = "/usr/sbin/service ${service_name} reload"
    }
    'RedHat', 'Amazon': {
      $package_name = 'riemann'
      $service_name = 'riemann'
      $config_dir = '/etc/riemann'
      $init_config_file = '/etc/sysconfig/riemann'
      case $::operatingsystemmajrelease {
        '6': {
          $reload_command = "/sbin/service ${service_name} reload"
        }
        '7': {
          $reload_command = "/bin/kill -HUP $(</var/run/riemann.pid)"
        }
        default: {
          fail("operatingsystemmajrelease `${::operatingsystemmajrelease}` not supported")
        }
      }
    }
    default: {
      fail("osfamily `${::osfamily}` not supported")
    }
  }
  $config_include_dir = 'custom'
  $init_config_hash = {}
  $log_file = '/var/log/riemann/riemann.log'
  $mail_from = "riemann@${::fqdn}"
  $mail_to = ["hanhao@cmhi.chinamobile.com"]
  $host_regex = undef
  $time_length = undef
  $time_granularity = undef
  $alarm_relation = undef
  $alarm_conditions = {}
}

# vim: ft=puppet
