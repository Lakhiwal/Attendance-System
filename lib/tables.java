<staff>create table staff(id int primary key, name varchar(67), doj varchar(56),sal float,desig varchar(56),address varchar(255), phone varchar(20),email varchar(45))</staff>

<course>create table course(id int primary key, cname varchar(50), totalsem int, duration int)</course>


<subjects>create table subjects (id varchar(45) primary key, cid varchar(56), sname varchar(56), 
sem varchar(10))</subjects>

<login>create table login(username varchar(100), password varchar(45), type varchar(30))</login>

<student>create table student(rollno varchar(56) primary key, name varchar(56), class varchar(56), sem varchar(45), emailid varchar(67), contact varchar(56), address varchar(255))</student>

<sattendance>create table sattendance(rollno varchar(56), date varchar(56), sub varchar(56), present varchar(56))</sattendance>
