create table user (
	id bigint not null AUTO_INCREMENT comment '自增Id',
	userId varchar(9) comment '用户id',
	userHead VARCHAR(16) COMMENT '用户头像',
	createTime timestamp null comment '创建时间',
	updateTime timestamp null comment '更新时间',
	userName varchar(64),
	PRIMARY KEY(id)
)ENGINE = INNODB CHARSET = utf8;


insert into user(id,userId,userhead,createtime,updateTime,username) values(1,'10001','1_04','2022-04-13 00:00:00','2022-04-13 00:00:00','pyip');

select * from user;
