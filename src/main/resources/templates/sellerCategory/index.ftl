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
                <form role="form" method="post" action="/sell/seller/category/save">
                    <div class="form-group">
                        <label>名称</label>
                        <input name="categoryName" value="${(category.categoryName)!''}" type="text" class="form-control" >
                    </div>
                    <div class="form-group">
                        <label>type</label>
                        <input name="categoryType" value="${(category.categoryType)!''}" type="number" class="form-control" >
                    </div>
                    <input type="hidden" name="categoryId" value="${(category.categoryId)!''}">
                    <button type="submit" class="btn btn-default">提交</button>
                </form>
            </div>
        </div>
    </div>
    </div>
</div>
</body>
</html>