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

Let's first install the packages we're going to need and afterwards perform the necessary configuration steps. The packages we're going to use are a DHCP server and a PPTP client. To install those package, run the following command and note that it may show that starting the DHCP server fails, because it has not been configured yet.

```
sudo apt-get install isc-dhcp-server pptp-linux
```

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

To activate those rules, we must load them into the kernel at startup. So let's write a script that does that. Create a new file `/etc/network/if-pre-up.d/iptables` and put the following lines into it:

```
#!/bin/sh
/sbin/iptables-restore < /etc/iptables.up.rules
```

Make the script executable by running:

```
sudo chmod +x /etc/network/if-pre-up.d/iptables
```

This script will be executed one the networking stack comes up. We're going to restart the network stack after configuring the IP address of the internal network interface, so for now we leave it this was.

Let's give the internal network interface an IP address. To do this, you need to select a range to use on this segment. It is adviced to use an address in a private range and to make sure it does not overlap with the range used on the external interface. If you don't have a lot of experience in networking, I'd suggest you pick an IP address of the form `192.168.n.1`, where `n` is a number between `1` and `250`. Use `ifconfig` to obtain the IP address of the external interface and, if it is of the form `192.168.n'.m`, use a value for `n` that is different from `n'`.

Now let's say we've picked `192.168.4.1` as IP address for the internal interface. Edit the file `/etc/network/interfaces` and find the line that starts with `iface eth0` (remember that we assume `eth0` is your internal interface). Change this file and the following lines that start with a space or tab (if there are any) into:

```
auto eth0
iface eth0 inet static
  address 192.168.4.1
  netmask 255.255.255.0
```

Make sure the `auto eht0` line is not duplicated if it was already there. To read the network interface configuration, you can restart the networking stack using:

```
sudo /etc/init.d/networking restart
```

At this point, a device on the internal network that has an IP address should be able to communicate with the internet. To hand out IP addresses using the DHCP server, we need to do a bit more configuration and after that we can do a test. First, edit the file `/etc/default/isc-dhcpserver` and find the line starting with `INTERFACES` and add your internal interface to it, for example:

```
INTERFACES="eth0"
```

At this point the DHCP server will be restricted to respond to requests on the given interface, but we need to specify in what way it needs to respond. Therefore edit the file `/etc/dhcp/dhcpd.conf`. Just leave everything as it is and add the following configuration to the bottom of the file:

```
option domain-name-servers 8.8.8.8;
subnet 192.168.4.0 netmask 255.255.255.0 {
  range 192.168.4.100 192.168.4.200;
  option routers 192.168.4.1;
```

We have now informed the DHCP server to hand out IP addresses in the range `192.168.4.100` - `192.168.4.200` and also send clients the IP address of the DNS server (8.8.8.8 is the Google DNS server) and the IP address of the router. Note that the router IP address is the IP address of the internal interface, since for connected devices that is the router. Also note that the subnet address is not the same as the IP address, but it ends in a `0` instead of a `1`.

At this point you should be able to connect a device on the internal interface and be able to reach the internet. Now we're going to add the configuration for a PPTP connection. Please make sure you know the IP address or the hostname of the provider and you also have the username and password for your account ready. Edit the file `/etc/ppp/chap-secrets` and add a line with the username, server name and password, e.g.

```
user1   provider2   password3
```

Also, we need to create a configuration file for the endpoint. The file is names after the provider, which we conveniently called `provider2`. So create a file `/etc/ppp/peers/provider2` and put the following lines in it (replacing `1.2.3.4` with the actual IP address or hostname of the VPN server, replacing `user1` with your username and the two occurences of `provider2` with the name of your provider):

```
pty "pptp 1.2.3.4 --nolaunchpppd"
name user1
remotename provider2
require-mppe-128
file /etc/ppp/options.pptp
ipparam provider2
```

> TODO
> PPTP client
> PPTP setup
> Android stuff

[raspberrypi]: http://www.raspberrypi.org/
[raspbian]: http://www.raspbian.org/
[networkconfiguration]: https://wiki.debian.org/NetworkConfiguration
[iptables]: https://wiki.debian.org/iptables
[dhcpserver]: https://wiki.debian.org/DHCP_Server
[pptpclient]: http://pptpclient.sourceforge.net/howto-debian.phtml
