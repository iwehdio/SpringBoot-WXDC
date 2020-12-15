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
                <form role="form" method="post" action="/sell/seller/product/save">
                    <div class="form-group">
                        <label>名称</label>
                        <input name="productName" value="${(productInfo.productName)!''}" type="text" class="form-control" >
                    </div>
                    <div class="form-group">
                        <label>库存</label>
                        <input name="productStock" value="${(productInfo.productStock)!''}" type="number" class="form-control" >
                    </div>
                    <div class="form-group">
                        <label>单价</label>
                        <input name="productPrice" value="${(productInfo.productPrice)!''}" type="text" class="form-control" >
                    </div>
                    <div class="form-group">
                        <label>描述</label>
                        <input name="productDescription" value="${(productInfo.productDescription)!''}" type="text" class="form-control" >
                    </div>
                    <div class="form-group">
                        <label>图片</label>
                        <img height="100" width="100" src="${(productInfo.productIcon)!''}" alt="">
                        <input name="productIcon" value="${(productInfo.productIcon)!''}" type="text" class="form-control" >
                    </div>
                    <div class="form-group">
                        <label>类目</label>
                        <select name="categoryType" class="form-control">
                            <#list categoryList as category>
                                <option value="${category.categoryType}"
                                    <#if productInfo.categoryType == category.categoryType>
                                        selected
                                    </#if>
                                >${category.categoryName}
                                </option>
                            </#list>
                        </select>
                    </div>
                    <input type="hidden" name="productId" value="${(productInfo.productId)!''}">
                    <button type="submit" class="btn btn-default">提交</button>
                </form>
            </div>
        </div>
    </div>
    </div>
</div>
</body>
</html>