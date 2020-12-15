<html>
    <head>
        <meta charset="utf-8">
        <link rel="stylesheet" href="/sell/css/style.css">
        <link href="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/3.3.5/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
    <div id="wrapper" class="toggled">
        <!--    侧边栏    -->
        <#include "common/nav.ftl">

        <!--    主体内容在这个div中   -->
        <div id="page-content-wrapper">
            <div class="container-fluid">
                <div class="row clearfix">
                    <div class="col-md-12 column">
                        <table class="table table-bordered table-hover">
                            <thead>
                            <tr>
                                <th>类名id</th>
                                <th>名字</th>
                                <th>type</th>
                                <th>创建时间</th>
                                <th>修改时间</th>
                                <th>操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list categoryList as category>
                            <tr>
                                <td>${category.categoryId}</td>
                                <td>${category.categoryName}</td>
                                <td>${category.categoryType}</td>
                                <td>${category.createTime}</td>
                                <td>${category.updateTime}</td>
                                <td>
                                    <a href="/sell/seller/category/index?categoryId=${category.categoryId}">修改</a>
                                </td>
                            </tr>
                            </#list >
                            </tbody>
                        </table>
                    </div>

        </div>
        </div>
        </div>
    </div>

    </body>
</html>