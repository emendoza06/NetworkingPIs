#!/bin/bash
echo Evanescence1 | sudo -S ip route add 10.0.0.1 via 10.0.0.5
echo Evanescence1 | sudo -S ip route add 10.0.0.2 via 10.0.0.1
echo Evanescence1 | sudo -S ip route add 10.0.0.3 via 10.0.0.5
echo Evanescence1 | sudo -S ip route add 10.0.0.4 via 10.0.0.3