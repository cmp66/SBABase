if exists (select * from dbo.sysobjects where id = object_id(N'[dbo].[divisions]') and OBJECTPROPERTY(id, N'IsUserTable') = 1)
drop table [dbo].[divisions]
GO

CREATE TABLE [dbo].[divisions] (
	[id] [int] NOT NULL ,
	[name] [varchar] (255) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL ,
	[leagueid] [int] NOT NULL 
) ON [PRIMARY]
GO

