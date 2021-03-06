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
          $reload_command = 'kill -HUP $(</var/run/riemann.pid)'
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
  $validate_cmd = '/usr/bin/java -cp /usr/lib/riemann/riemann.jar riemann.bin test %'
  $config_include_dir = 'conf.d'
  $init_config_hash = {}
  $log_file = '/var/log/riemann/riemann.log'
}

# vim: ft=puppet
