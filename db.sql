create table online(
	id int unsigned not null auto_increment comment 'id',
	number int unsigned not null default 0 comment '在线人数',
	term varchar(32) not null default '' comment '期次号',
	prize varchar(32) not null default '' comment '中奖号',
	change_num int not null default 0 comment '波动',
	time int unsigned not null default 0 comment '时间',
	PRIMARY KEY (`id`),
	unique key unique_term (`term`)
)engine=innodb default charset=utf8 comment '在线数据表';