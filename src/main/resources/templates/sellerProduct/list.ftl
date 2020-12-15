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
                                <th>商品id</th>
                                <th>名称</th>
                                <th>图片</th>
                                <th>单价</th>
                                <th>库存</th>
                                <th>描述</th>
                                <th>类目</th>
                                <th>创建时间</th>
                                <th>修改时间</th>
                                <th colspan="2">操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list productInfoPage.content as productInfo>
                            <tr>
                                <td>${productInfo.productId}</td>
                                <td>${productInfo.productName}</td>
                                <td><img height="100" width="100" src="${productInfo.productIcon}" alt=""></td>
                                <td>${productInfo.productPrice}</td>
                                <td>${productInfo.productStock}</td>
                                <td>${productInfo.productDescription}</td>
                                <td>${productInfo.categoryType}</td>
                                <td>${productInfo.createTime}</td>
                                <td>${productInfo.updateTime}</td>
                                <td>
                                    <a href="/sell/seller/product/index?productId=${productInfo.productId}">修改</a>
                                </td>
                                <td>
                                    <#if productInfo.getProductStatusEnum().getMessage() =="上架">
                                    <a href="/sell/seller/product/offSale?productId=${productInfo.productId}">下架</a>
                                    <#else>
                                    <a href="/sell/seller/product/onSale?productId=${productInfo.productId}">上架</a>
                                </#if>
                                </td>
                            </tr>
                            </#list >
                            </tbody>
                        </table>
                    </div>
                    <div class="col-md-12 column">
                        <ul class="pagination pull-right">
                            <#if currentPage lte 1>
                            <li class="disabled"><a href="#">上一页</a></li>
                            <#else>
                            <li><a href="/sell/seller/product/list?page=${currentPage-1}&size=${size}">上一页</a></li>
                        </#if>
                        <#list 1..productInfoPage.getTotalPages() as index>
                        <#if currentPage == index>
                        <li class="disabled"><a href="#">${index}</a></li>
                        <#else>
                        <li><a href="/sell/seller/product/list?page=${index}&size=${size}">${index}</a></li>
                    </#if>
                </#list>
                <#if currentPage gte productInfoPage.getTotalPages()>
                <li class="disabled" ><a href="#">下一页</a></li>
                <#else>
                <li><a href="/sell/seller/product/list?page=${currentPage+1}&size=${size}">下一页</a></li>
            </#if>
            </ul>
        </div>
        </div>
        </div>
        </div>
    </div>

    </body>
</html>