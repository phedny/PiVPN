PiVPN
=====

PiVPN is an Android app that can be used to enable and disable VPN connections that are managed by a Raspberry Pi. The Raspberry Pi has two network adapter: one which is connected to the internet and to the other one you connect one or more devices.

For example, your Raspberry Pi has a WiFi dongle to connect to an existing network with an internet connection. A TV is connected to the wired interface on the Raspberry Pi.

What do you need?
-----------------

* A [Raspberry Pi][raspberrypi]
* A second network adapter, which can be either a wired or a wireless one

How to prepare your Raspberry Pi
--------------------------------

You're free to use any image as base for your Raspberry Pi, but this instruction assumes you're using a clean and up-to-date [Raspbian][raspbian] installation, have performed all steps to get a working shell and have a working internet connection on one network adapter.

Please not that these instruction do not setup firewall rules related to security, only the minimum to Make It Work. The instructions assume `wlan0` is your external interface connected to the internet, `eth0` is the interface you connect devices to and `ppp0` will be the interface for your VPN connection. Modify all files and commands according to your setup.

Open a shell on your Raspberry Pi

To enable IP packet forwarding, edit `/etc/sysctl.conf` and uncomment the line 

```
net.ipv4.ip_forward=1
```

The above step ensures that packet forwarding is enabled when your Raspberry Pi boots. To load the configuration right now, execute the following command:

```sh
sysctl --system
```

Right now, your Raspberry Pi will forward IP packets from one interface to the other, if requested by the sender of a packet. However, other network components do not know about the existence of the subnet behind the Raspberry Pi, so they will be unable to route packets back. The easiest way to solve this, is to enable Network Address Translation on your Raspberry Pi. Create a file `/etc/iptables.up.rules` with the following content:

```
*nat

-A POSTROUTING -o wlan0 -j MASQUERADE
-A POSTROUTING -o ppp0 -j MASQUERADE

COMMIT
```

The file above added two rules to the `POSTROUTING` chain of the `nat` iptables tables. The first rule makes sure the NAT is performed on all connections that are routing to your wireless interface. The second rule does the same for the VPN connection, which will be created in a moment.

> TODO
> DHCP server
> PPTP client
> PPTP setup
> Android stuff

[raspberrypi]: http://www.raspberrypi.org/
[raspbian]: http://www.raspbian.org/
