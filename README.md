###################################################
#### Ant Miner monitor tool V1.00
###################################################
# This tool is a stand alone client which can run on linux / windows (Java 1.6 or higher needed)
#  you will need to schedule the run.bat / run.sh yourself as a crontab or windows scheduled job 
# 
# Not mend to run on an Antminer itself!
# for each Antminer you need to create a new X.properties file where X can be anything
# The location of the properties file is the conf directory.
# Example for my L3+ I made L3plus.properties
# in the properties file all values which you see below can be configured
# I described all values of which I know what they mean
# If an value is > or < then and alertmail will be sent from alert.antminer@gmail.com
# Manual action needs to be taken, this tool is purely mend to notify you in case of an issue / temp increase etc.
# you do not need to set all values, if you would only like to have a notification if the average mining speed drops below a certain value
# then a properties file with only one value in it would be sufficient (example : GHS av<450.69 ) 
#
###################################################
#
# As is did this in my spare time a donation for a cup of coffee is much appreciated! 
#
#	Gulden address:   Gfn2czWKxKbzyFjMDCQ8X8kEzUFe3kb5T6
#	Reddcoin address: Rrfer4nLp7sZAebyKZKTuqdJvoQcaic5Da
#	Ripple address:   rpDXk9PjQoKWq4JQxKuWw1GGSTfoFtJZoK
#	Litecoin address: LRUpankDbdhwwSYDpf5sZ8fNSkgKj45skX
#
###################################################
#
# Properties file: (Required properties)
# The minimum required properties are:

---------------------------------------------------------
userName=<username of the antminer>
password=<password of the antminer>
ip=<ip of the antminer>
recipients=<email adress where to receive the alert mails>
---------------------------------------------------------

# The alert conditions need to be defined after the line #ALERT in the properties file.
#  example:

---------------------------------------------------------
#ALERT
temp1>60
chain_acs1 ISNOT oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo
---------------------------------------------------------

###################################################
# Test your first setup
# - Edit the example.properties in conf directory
# - Change the Required properties
# - Run the run.bat or run.sh
# - after correct configuration, you should receive an e-mail.
###################################################
#
# Below all the possible alert conditions and the description. known by us
# You can use the following operators with all numeric values: 
#	> for Bigger then value
#	< for Smaller then value
#       = for exact match 
#	ISNOT for chain_acs values only (oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo)
#		See example of chain_acs below

  

#Unknown
STATS=0
#Antminertype
ID=L30
#Uptime (in seconds)
Elapsed=189184
#Unknown
Calls=0
#Unknown
Wait=0.000000
#Unknown
Max=0.000000
#Unknown
Min=99999999.000000
#Current Speed (Mhz)
GHS 5s=503.459
#Average Speed (Mhz)
GHS av=500.69
#Amount of PCB / chains (not 100% sure)
miner_count=4
#Running frequency 384 is default for L3+ so no overclocking
frequency=384
#amount of fans
fan_num=2
#fan 1 speed
fan1=3630
#fan 1 speed
fan2=3660
#amount of PCB 
temp_num=4
#temp for each PCB
temp1=45
temp2=44
temp3=42
temp4=43
#Temp for each chip
temp2_1=52
temp2_2=51
temp2_3=49
temp2_4=50
#Unknown 
temp31=0
temp32=0
temp33=0
temp34=0
#Unknown 
temp4_1=0
temp4_2=0
temp4_3=0
temp4_4=0
temp_max=45
#Unknown 
Device Hardware%=0.0000
#Unknown 
no_matching_work=144
#Amount of chips per PCB
chain_acn1=72
chain_acn2=72
chain_acn3=72
chain_acn4=72

###################################################
###		!!!READ THIS PLEASE!!!		###
#		Most important value
#all status of the 288 chips inside the L3 (4x 72)
#!!!if you have a different miner then an L3+ you will have different amount of chips!!! 
#open a web browser go to you miner, then go to the tab "miner status" and copy / paste each line which you see under ASIC status
#this has to be a match, else you will get notified by email constantly

chain_acs1 ISNOT oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo
chain_acs2 ISNOT oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo
chain_acs3 ISNOT oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo
chain_acs4 ISNOT oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo

#if for example you know 1 chip is broken in PCB 2 and dont want this notification you can do the following :
#chain_acs2= oooooooo oooooXoo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo oooooooo
#This way the string matches and will be ignored by the monitoring tool

#HW faults by PCB 
chain_hw1=143
chain_hw2=0
chain_hw3=0
chain_hw4=1
#speed by PCB (in Mhz)
chain_rate1=126.23
chain_rate2=126.28
chain_rate3=125.18
chain_rate4=125.78

