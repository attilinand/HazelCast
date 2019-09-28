# HazelCast
HazelCast
This project contains 3 modules.which gives basic idea about hazelcast setup to quickly start with client server architecture.

How to Run:
download or checkout the project from github.
import all 3 modules into eclipse or sts.build using maven clean install command.
each module is a seperate spring boot project which can be started individually.

1)storagenode:This module contains storage node config with hazelcast map store.MapStore is used to write the content from hazelcast
cache into any underlying DB for persistance asynchronously or synchronously.
2)client:this module contains using hazelcast from the client instance perspective.
3)shared:this module contains the shared resources which are required for both client and storage node.
