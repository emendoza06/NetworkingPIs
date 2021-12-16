#!/bin/bash
cd ~
ssh epharra1@10.0.0.1 'bash ~/pingTest.sh'
ssh epharra2@10.0.0.2 'bash ~/pingTest.sh'
ssh epharra3@10.0.0.3 'bash ~/pingTest.sh'