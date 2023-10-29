--drop database journal_db;
--create database journal_db;
use journal_db;
/*
-- 0) Direction
-- 1) Predmet
create table subjectTable(
						id bigint identity(1, 1) not for replication primary key,
						sName varchar(50) not null unique
						);
-- 2) Group
create table group_table(
						id bigint not null identity(1, 1) not for replication primary key,
						id_dir int not null FOREIGN KEY REFERENCES direction(id),
						semester varchar(1) not null,
						year varchar(9) not null default('2023-2024')
 						);
-- 3) Discipline
create table discipl(
						id bigint not null identity(1, 1) not for replication primary key,
						subject_name varchar(50) not null FOREIGN KEY REFERENCES subjectTable(sName),
						id_group bigint not null FOREIGN KEY REFERENCES group_table(id),
						countLK int not null,
						countPZ int not null,
						countLB int not null,
						formControl varchar(1) not null
		);

-- 4) Student
create table student(
	id bigint not null identity(1, 1) not for replication primary key,
	fio varchar(55),
	id_group bigint not null FOREIGN KEY REFERENCES group_table(id)
);

-- 5) Journal
create table journal(
	id bigint not null identity(1, 1) not for replication primary key,
	id_discipl bigint not null FOREIGN KEY REFERENCES discipl(id),
	date_str varchar(10) not null,
	type_pair int not null default(0),
	theme varchar(555) not null
);

--6) Journal content
create table journal_content(
	id_journal bigint not null FOREIGN KEY REFERENCES journal(id),
	id_student bigint not null FOREIGN KEY REFERENCES student(id),
	is_absent int default(0),
	mark int
);
*/

/*
insert into direction(dir_name) values
		('Прикладная математика и Информатика'),
		('Геология'),
		('Лингвистика');

insert into group_table(id_dir, semester, year) values
		(1, 7, '2023-2024'),
		(1, 5, '2023-2024'),
		(2, 1, '2023-2024'),
		(3, 3, '2023-2024');

insert into student(fio, id_group) values
		('Абдуазимов Сафарали Музаффарович', 1),
		('Ахатзода Техроншох Абдухабиб', 1),
		('Ахрорзода Шохрух Ахрорович', 1);

insert into subjectTable (sName) values
		('Разработка база данных: MS SQL Server'),
		('Java технологии'),
		('ЭВМ');

insert into discipl(subject_name, id_group, countLK, countPZ, countLB, formControl) values
		('Java технологии', 1, 18, 36, 0, 'З'),
		('Разработка база данных: MS SQL Server', 2, 18, 36, 0, 'Э');

insert into journal(id_discipl, date_str, type_pair, theme) values
		(1, '12-10-2023', 1, 'Реализовать программу в виде калькулятора'),
		(1, '14-10-2023', 0, 'Работа с Базом Данныхом'),
		(1, '17-10-2023', 1, 'Реализовать электронный журнал');

insert into journal_content(id_journal, id_student, is_absent, mark) values
		(1, 2, 0, 100),
		(2, 1, 1, 0);
*/

IF NOT EXISTS (SELECT * FROM journal_content WHERE id_journal = 1 AND id_student = 3) BEGIN insert into journal_content(id_journal, id_student, is_absent, mark) values(1, 3, 0, 95); END ELSE BEGIN UPDATE journal_content SET mark = 90 WHERE id_journal = 1 AND id_student = 3; END

SELECT j.*, s.fio FROM journal_content j, student s where s.id = j.id_student;

SELECT type_pair, COUNT(type_pair) as count FROM journal WHERE id_discipl = 1 GROUP BY type_pair; 

SELECT * FROM journal;