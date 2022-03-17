insert into users(user_id,password,name,email)
values ('terry','1','테리','terry@cs.com');

insert into articles(title,contents,user_id,created_date)
values('제목1','내용1','terry',now());