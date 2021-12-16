#!/bin/bash
cd ~
ssh epharra1@10.0.0.1 'bash ~/pingTest.sh'
ssh epharra1@10.0.0.1 'bash ~/SCPTest.sh'
ssh epharra2@10.0.0.2 'bash ~/pingTest.sh'
ssh epharra2@10.0.0.2 'bash ~/SCPTest.sh'
ssh epharra3@10.0.0.3 'bash ~/pingTest.sh'
ssh epharra3@10.0.0.3 'bash ~/SCPTest.sh'
ssh epharra4@10.0.0.4 'bash ~/pingTest.sh'
ssh epharra4@10.0.0.4 'bash ~/SCPTest.sh'
ssh epharra5@10.0.0.5 'bash ~/pingTest.sh'
ssh epharra5@10.0.0.5 'bash ~/SCPTest.sh'