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
                                <th>订单id</th>
                                <th>姓名</th>
                                <th>手机号</th>
                                <th>地址</th>
                                <th>金额</th>
                                <th>订单状态</th>
                                <th>支付状态</th>
                                <th>创建时间</th>
                                <th colspan="2">操作</th>
                            </tr>
                            </thead>
                            <tbody>
                            <#list orderDTOPage.content as orderDTO>
                            <tr>
                                <td>${orderDTO.orderId}</td>
                                <td>${orderDTO.buyerName}</td>
                                <td>${orderDTO.buyerPhone}</td>
                                <td>${orderDTO.buyerAddress}</td>
                                <td>${orderDTO.orderAmount}</td>
                                <td>${orderDTO.getOrderStatusEnum().getMessage()}</td>
                                <td>${orderDTO.getpayStatusEnum().getMessage()}</td>
                                <td>${orderDTO.createTime}</td>
                                <td>
                                    <a href="/sell/seller/order/detail?orderId=${orderDTO.orderId}">详情</a>
                                </td>
                                <td>
                                    <#if orderDTO.getOrderStatusEnum().getMessage() =="新订单">
                                    <a href="/sell/seller/order/cancel?orderId=${orderDTO.orderId}">取消</a>
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
                            <li><a href="/sell/seller/order/list?page=${currentPage-1}&size=${size}">上一页</a></li>
                        </#if>
                        <#list 1..orderDTOPage.getTotalPages() as index>
                        <#if currentPage == index>
                        <li class="disabled"><a href="#">${index}</a></li>
                        <#else>
                        <li><a href="/sell/seller/order/list?page=${index}&size=${size}">${index}</a></li>
                    </#if>
                </#list>
                <#if currentPage gte orderDTOPage.getTotalPages()>
                <li class="disabled" ><a href="#">下一页</a></li>
                <#else>
                <li><a href="/sell/seller/order/list?page=${currentPage+1}&size=${size}">下一页</a></li>
            </#if>
            </ul>
        </div>
        </div>
        </div>
        </div>
    </div>

    <div class="modal fade" id="msgmodal" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                    <h4 class="modal-title" id="myModalLabel">
                        新订单
                    </h4>
                </div>
                <div class="modal-body" id="modal-body">
                    订单号：
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">关闭</button>
                    <button onclick="location.reload()" type="button" class="btn btn-primary">确认</button>
                </div>
            </div>

        </div>

    </div>


    <script src="https://cdn.bootcdn.net/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://cdn.bootcdn.net/ajax/libs/twitter-bootstrap/3.3.5/js/bootstrap.min.js"></script>
    <script>
        var webSocket = null;
        if('WebSocket' in window) {
            webSocket = new WebSocket('ws://后端部署ip地址/sell/webSocket');
        } else {
            alert('不支持WebSocket');
        }
        webSocket.onopen = function (event) {
            console.log('建立连接');
        };
        webSocket.onclose = function (event) {
            console.log('连接关闭');
        };
        webSocket.onmessage = function (event) {
            console.log('收到消息'+event.data);
            $('#modal-body').text($('#modal-body').text()+ event.data);
            $("#msgmodal").modal('show');
        };
        webSocket.onerror = function (event) {
            console.log('出错');
        };
        window.onbeforeunload = function () {
            webSocket.close();
        };
    </script>
    </body>
</html>