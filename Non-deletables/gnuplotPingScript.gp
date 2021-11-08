set terminal png
set output "./GraphedResults/filePing.png"
set title "Ping rtt"
show title
set xlabel "ping count"
set ylabel "time in ms"
plot "./pingData.dat" using 1:2 w lp title "rtt of each ping",\
        "./pingData.dat" using 1:3 w lp title "avg rtt"
exit