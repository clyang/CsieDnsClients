# csie.io DDNS client for Mac/Windows
本repository內包含csie.io Dynamic Dns服務中所使用的Windows以及Mac更新client，以下對各平台的code做簡單的說明。

## Windows Client說明
Windows client完全是基於[DuckDNSClient](https://github.com/JozefJarosciak/DuckDNSClient) (使用的License為GPLv2) 修改而成，是由Java撰寫而成，核心的部分僅只有修改update時所使用的URL，其餘的部分僅給予中文化，換上icon以及小修一些bug而已。

![csie.io windows client](https://csie.io/instimg/win1.png)


## Mac Client說明
Mac Client使用Swift開發，是我人生中第一隻OSX Application，所以如果coding style不符合常見的pattern還請見諒，若您願意改善它，也很歡迎直接發pull request給我。

![csie.io mac client](https://csie.io/instimg/mac4.png)

