<%@ page import="com.emprogria.utils.W3InfoLib" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<%--
  Created by IntelliJ IDEA.
  User: Abner Wang
  Date: 2020/6/23
  Time: 上午 09:46
  To change this template use File | Settings | File Templates.
--%>
<html>
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <title>農糧署資料上傳 | 線上訂購平台</title>
    <!-- favicon -->
    <link rel="shortcut icon" href="images/favicon.ico" />
    <!-- Tell the browser to be responsive to screen width -->
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- Font Awesome -->
    <link rel="stylesheet" href="plugins/fontawesome-free/css/all.min.css"/>
    <!-- Ionicons -->
    <link rel="stylesheet" href="plugins/ionicons/ionicons.css">
    <!-- adminLte -->
    <link href="plugins/AdminLTE-3.0.5/css/adminlte.min.css" rel="stylesheet"/>
    <!-- Custom style -->
    <link rel="stylesheet" href="css/jquery-plugin.css">
    <!-- toastr -->
    <link rel="stylesheet" href="plugins/toastr/build/toastr.css">


</head>
<body class="hold-transition sidebar-mini">
<%
    String sessionId = session.getId();
    String sesame = request.getParameter("sid");
    Object lockerId = "";

  if (sesame != null) {
        lockerId = W3InfoLib.getLockerID(sessionId, sesame);
    }
%>
<div class="d-none" id="lockerId"><%=lockerId%></div>

<!-- Site wrapper -->
<!-- Site wrapper -->
<div class="wrapper">
    <!-- Navbar -->
    <nav class="main-header navbar navbar-expand navbar-white navbar-light">
        <!-- Content Header (Page header) -->
        <!-- Left navbar links -->
        <ul class="navbar-nav">
            <li class="nav-item">
                <a class="nav-link" data-widget="pushmenu" href="#" role="button"><i class="fas fa-bars"></i></a>
            </li>
        </ul>
        <h1>農糧署資料上傳</h1>
        <!-- Right navbar links -->
        <ul class="navbar-nav ml-auto">
            <li class="breadcrumb-item">客戶預購單作業</li>
            <li class="breadcrumb-item active">農糧署資料上傳</li>
        </ul>
    </nav>
    <!-- Main Sidebar Container -->
    <aside class="main-sidebar sidebar-dark-primary elevation-4">
        <!-- Brand Logo -->
        <a href="#" class="brand-link">
         <img src="./images/Logo.png"
                 alt="Taifer Logo"
                 class="brand-image img-circle elevation-3"
                 style="opacity: .8">
            <span class="brand-text font-weight-light">線上訂購平台</span>
        </a>
        <!-- Sidebar -->
        <div class="sidebar">
            <!-- Sidebar user (optional) -->
            <div class="user-panel mt-3 pb-3 mb-3 d-flex">
                <div class="image">
                    <img src="plugins/AdminLTE-3.0.5/img/user2-160x160.jpg" class="img-circle elevation-2"
                         alt="User Image">
                </div>
                <div class="info">
                    <a href="#" class="d-block" id="loginName"></a>
                </div>
            </div>
            <!-- Sidebar Menu -->
            <nav class="mt-2" id="sidebar"></nav>
        </div>
    </aside>
    <!-- Content Wrapper. Contains page content -->
    <div class="content-wrapper">
        <!-- Main content -->
        <section class="content">
            <div class="container-fluid">
                <div class="row">
                    <div class="col-md-6">
                        <form action="T001-JC3.jsp" method="post" enctype="multipart/form-data">
                            <div class="card card-info">
                                <div class="card-header container-fluid">
                                    <div class="row align-items-center">
                                        <h3 class="card-title">農糧署資料上傳</h3>
                                    </div>
                                </div><!-- /.card-header -->
                                <div class="card-body">
                                    <div class="input-group mb-3">
                                        <div class="input-group-prepend">
                                            <button class="btn btn-outline-secondary" type="button"
                                                    onclick="generateFile()">執行
                                            </button>
                                        </div>
                                        <div class="custom-file">
                                            <input type="file" class="custom-file-input" id="uploadPreOrders"
                                                   name="uploadFile"
                                                   accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">
                                            <label class="custom-file-label" for="uploadPreOrders">選擇檔案</label>
                                        </div>
                                    </div>
                                </div><!-- /.card-body -->
                            </div><!-- /.card -->
                        </form>
                    </div>
                    <div class="col-md-6">
                        <form action="T001-JC3.jsp" method="post" enctype="multipart/form-data">
                            <div class="card card-info">
                                <div class="card-header container-fluid">
                                    <div class="row align-items-center">
                                        <h3 class="card-title">刪除農糧署資料</h3>
                                    </div>
                                </div><!-- /.card-header -->
                                <div class="card-body">
                                    <div class="input-group mb-3">
                                        <div class="input-group-prepend">
                                            <button class="btn btn-outline-secondary" type="button"
                                                    onclick="generateFile_del()">執行
                                            </button>
                                        </div>
                                        <div class="custom-file">
                                            <input type="file" class="custom-file-input" id="deletePreOrders"
                                                   name="deleteFile"
                                                   accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet">>
                                            <label class="custom-file-label"
                                                   for="deletePreOrders">選擇檔案</label>
                                        </div>
                                    </div>
                                </div><!-- /.card-body -->
                            </div><!-- /.card -->
                        </form>
                    </div>
                </div>
                <div class="card card-info card-outline">
                    <div class="card-body">
                        <div class="table-responsive">
                            <table id="useDataTables" class="table table-bordered table-striped">
                                <thead>
                                <tr>
                                    <th class="text-nowrap">
                                        序號
                                    </th>
                                    <th class="text-nowrap">
                                        錯誤訊息
                                    </th>
                                </tr>
                                </thead>
                                <tbody id="errorMsg-list"></tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div><!-- /.container-fluid-->
        </section>
    </div>
    <footer class="main-footer" id="main-footer"></footer>
    <!-- Control Sidebar -->
    <aside class="control-sidebar control-sidebar-dark">
        <!-- Control sidebar content goes here -->
    </aside>
</div>
<!-- jquery -->
<script src="js/jquery.min.js"></script>
<!-- bootstrap -->
<script src="js/bootstrap.min.js"></script>
<!-- adminLTE App -->
<script src="plugins/AdminLTE-3.0.5/js/adminlte.min.js"></script>
<!-- form-serializer -->
<script src="plugins/form-serializer/dist/jquery.serialize-object.min.js"></script>
<!-- jquery-validation -->
<script src="plugins/jquery-validation/dist/jquery.validate.min.js"></script>
<script src="plugins/jquery-validation/dist/localization/messages_zh_TW.min.js"></script>
<!-- datatables -->
<script src="plugins/datatables/datatables.net/js/jquery.dataTables.min.js"></script>
<script src="plugins/datatables/datatables.net-bs4/js/dataTables.bootstrap4.min.js"></script>
<script src="plugins/datatables/datatables.net-responsive/js/dataTables.responsive.min.js"></script>
<script src="plugins/datatables/datatables.net-responsive-bs4/js/responsive.bootstrap4.min.js"></script>
<!-- toastr -->
<script src="plugins/toastr/toastr.js"></script>

<!-- custome js-->
<script src="js/app/w3info.js"></script>
<script src="js/jquery-plugin.js"></script>
<script src="js/T001-JC1.js"></script>

</body>
</html>