请求地址
根据短链接跳转
GET
http://localhost:8080/{shortCode}
输入下方url跳转到www.baidu.com
http://localhost:8080/abc123

创建短链接：
POST:
http://localhost:8080/
可选输入TOKEN，带TOKEN时创建对应用户的短链接，无TOKEN时自动生成一个TOKEN并存在COOKIE中
HEADR:
{
    token=46e342918dea4e3daadee26c9fbe8c43;
}
BODY
{
  "originalUrl": "https://www.baidu.com",
  "customCode":"abc123"//可选自定义code
}
成功后返回
{
    "shortCode": "abc123",
    "originalUrl": "https://www.baidu.com",
    "shortUrl": "http://localhost:8080/abc123"
}

获取用户短链接
GET
http://localhost:8080/links
headr:
{
    token:46e342918dea4e3daadee26c9fbe8c43;
}
成功后返回：
[
    {
        "shortCode": "53e52607",
        "originalUrl": "https://www.baidu.com"
    },
    {
        "shortCode": "054f3de8",
        "originalUrl": "https://www.baidu.com"
    },
    {
        "shortCode": "dde79f14",
        "originalUrl": "https://www.baidu.com"
    }
]
删除短链接
DELETE
/links/delete/
需带token校验为空会从cookie取
headr:
{
    token:46e342918dea4e3daadee26c9fbe8c43;
}
PARAMS:{
    shortCode
}
示例：http://localhost:8080/links/delete/?shortCode=b59723da
