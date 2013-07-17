#!/usr/local/bin/thrift --gen java

namespace java edu.berkeley.bailis.thrifttest

service ThriftTestService {
  void nop(1: binary data);
}
