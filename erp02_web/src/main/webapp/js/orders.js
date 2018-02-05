//静态传参
var oper=Request['oper'];
//标识采购还是销售
var type = Request['type'] * 1;
var btnText = "采购申请";
var typeText = type == 1?"入库":"出库";
$(function(){	
	initTitle();	
	initGrid();	
	initOrdersDlg();	
	initOrderdetailGrid();
	//入库明细窗口
	$('#itemDlg').dialog({
		title:typeText,
		width:300,
		height:200,
		modal:true,
		closed:true,
		buttons:[
	     {
	    	 text: typeText,
	    	 iconCls:'icon-save',
	    	 handler:doInOutStore
	     }
		]
	});
	
	//新增订单窗口
	$('#addOrdersDlg').dialog({
		title:btnText,
		width:700,
		height:400,
		closed:true,
		modal:true
	});
	
	//显示供应商或客户
	if(type == 1){
		$('#addOrdersSupplierName').html("供应商");
	}
	if(type == 2){
		$('#addOrdersSupplierName').html("客户");
	}
});

/**
 * 获取订单列表的配置信息
 */
function getDataGridCfg(){
	var columns = getColumns();
	return {
		columns : columns,
		singleSelect : true,
		pagination:true,
		onDblClickRow:function(rowIndex, rowData){
			//双击行事件，rowIndex行的索引，rowData行的数据			
			//打开详情窗口
			$('#ordersDlg').dialog('open');			
			$('#uuid').html(rowData.uuid);
			$('#supplierName').html(rowData.supplierName);
			$('#state').html(formatState(rowData.state));
			$('#createrName').html(rowData.createrName);
			$('#checkerName').html(rowData.checkerName);
			$('#starterName').html(rowData.starterName);
			$('#enderName').html(rowData.enderName);
			$('#createtime').html(formatDate(rowData.createtime));
			$('#checktime').html(formatDate(rowData.checktime));
			$('#starttime').html(formatDate(rowData.starttime));
			$('#endtime').html(formatDate(rowData.endtime));	
			
			//运单号
			$('#waybillsn').html(rowData.waybillsn);
			//加载明细的数据
			$('#itemgrid').datagrid('loadData',rowData.orderDetails);
		}
	};
}

/**
 * 获取订单列表列的设置
 */
function getColumns(){
	//采购
	if(type == 1){
		return [ [ 
			{field:'uuid',title:'编号',width:100},
			{field:'createtime',title:'生成日期',width:100,formatter:formatDate},
			{field:'checktime',title:'审核日期',width:100,formatter:formatDate},
			{field:'starttime',title:'确认日期',width:100,formatter:formatDate},
			{field:'endtime',title:'入库日期',width:100,formatter:formatDate},
			{field:'createrName',title:'下单员',width:100},
			{field:'checkerName',title:'审核员',width:100},
			{field:'starterName',title:'采购员',width:100},
			{field:'enderName',title:'库管员',width:100},
			{field:'supplierName',title:'供应商',width:100},
			{field:'totalmoney',title:'合计金额',width:100},
			{field:'state',title:'状态',width:100,formatter:formatState}
		] ];
	}
	//销售
	if(type == 2){
		return [ [ 
					{field:'uuid',title:'编号',width:100},
					{field:'createtime',title:'生成日期',width:100,formatter:formatDate},
					{field:'endtime',title:'出库日期',width:100,formatter:formatDate},
					{field:'createrName',title:'下单员',width:100},
					{field:'enderName',title:'库管员',width:100},
					{field:'supplierName',title:'客户',width:100},
					{field:'totalmoney',title:'合计金额',width:100},
					{field:'state',title:'状态',width:100,formatter:formatState},//formatter:接收类型 function类型
					{field:'waybillsn',title:'运单号',width:100}
				] ];
	}
}

/**
 * 订单状态的格式化器
 * @param value
 * @returns {String}
 */
function formatState(value){
	//采购: 0:未审核 1:已审核, 2:已确认, 3:已入库；销售：0:未出库 1:已出库
	if(type == 1){
		switch(value * 1){
			case 0:return '未审核';
			case 1:return '已审核';
			case 2:return '已确认';
			case 3:return '已入库';
		}
	}
	if(type == 2){
		switch(value * 1){
			case 0:return '未出库';
			case 1:return '已出库';
		}
	}
}

/**
 * 日期格式化器
 * @param value
 * @returns
 */
function formatDate(value){
	if(value){//value有值的情况下
		return new Date(value).Format('yyyy-MM-dd');
	}
	return "";
}

/**
 * 明细的状态
 * @param value
 * @returns {String}
 */
function formatDetailState(value){
	if(type == 1){
		switch(value * 1){
			case 0:return '未入库';
			case 1:return '已入库';
		}
	}
	if(type == 2){
		switch(value * 1){
			case 0:return '未出库';
			case 1:return '已出库';
		}
	}
}

/**
 * 审核
 */
function doCheck(){
	$.messager.confirm('确认','确认要审核吗?',function(yes){
		if(yes){
			//提交审核
			$.ajax({
				url : 'orders_doCheck.action',//请求的url
				data : {id:$('#uuid').html()},//订单的编号
				dataType : 'json',//返回的数据类型: jquery把返回的值转成json数据
				type : 'post',//请求的方式
				success : function(rtn) {//请求成功时的回调函数, rtn就服务端响应回来的数据
					$.messager.alert('提示', rtn.message, 'info', function() {
						if(rtn.success){
							//关闭详情窗口
							$('#ordersDlg').dialog('close');
							//刷新未审核列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 确认
 */
function doStart(){
	$.messager.confirm('确认','确定要确认吗?',function(yes){
		if(yes){
			//提交确认
			$.ajax({
				url : 'orders_doStart.action',//请求的url
				data : {id:$('#uuid').html()},//订单的编号
				dataType : 'json',//返回的数据类型: jquery把返回的值转成json数据
				type : 'post',//请求的方式
				success : function(rtn) {//请求成功时的回调函数, rtn就服务端响应回来的数据
					$.messager.alert('提示', rtn.message, 'info', function() {
						if(rtn.success){
							//关闭详情窗口
							$('#ordersDlg').dialog('close');
							//刷新已审核列表
							$('#grid').datagrid('reload');
						}
					});
				}
			});
		}
	});
}

/**
 * 出入库
 */
function doInOutStore(){
	$.messager.confirm('确认','确认要' + typeText + '吗?',function(yes){
		if(yes){
			var submitData = $('#itemForm').serializeJSON();
			var url = 'orderdetail_doInStore.action';			
			if(type == 2){
				//销售出库
				url = 'orderdetail_doOutStore.action';
			}
			$.ajax({
				url : url,//请求的url
				data : submitData,//查询条件数据，请求的数据
				dataType : 'json',//返回的数据类型: jquery把返回的值转成json数据
				type : 'post',//请求的方式
				success : function(rtn) {//请求成功时的回调函数, rtn就服务端响应回来的数据
					$.messager.alert('提示', rtn.message, 'info', function() {
						if(rtn.success){
							//关闭入库窗口
							$('#itemDlg').dialog('close');
							//获取选中的行
							var row = $('#itemgrid').datagrid('getSelected');
							row.state = '1';//修改状态为已入库
							
							//手式刷新
							var data = $('#itemgrid').datagrid('getData');
							$('#itemgrid').datagrid('loadData',data);
							
							//是否所有明细都已经入库
							var flag = true;
							$.each(data.rows,function(i,r){
								if(r.state == '0'){
									flag = false;
									return false;//退出循环 不是break;
								}
							});
							
							if(flag == true){
								//关闭订单窗口
								$('#ordersDlg').dialog('close');
								//刷新已确认的订单列表
								$('#grid').datagrid('reload');
							}
						}
					});
				}
			});
		}
	});
}

/**
 * 订单列表
 */
function initGrid(){
	//订单列表的配置信息
	var datagridCfg = getDataGridCfg();
	var url = "";
	switch(oper){
		case 'orders': url="orders_listByPage.action?";break;
		case 'returnorders': url="returnorders_listByPage.action?";
			datagridCfg.toolbar=[];
			if(type == 2){
				btnText="销售退货登记";
			}
			datagridCfg.toolbar.push({
				text:btnText,
				iconCls:'icon-remove',
				handler:function(){
					//弹出采购申请窗口
					$('#addOrdersDlg').dialog('open');
				}
			});	
		break;
		case 'myorders': url="orders_myListByPage.action?";
			datagridCfg.toolbar=[];
			if(type == 2){
				btnText="销售订单录入";
				
			}
			datagridCfg.toolbar.push({
				text:btnText,
				iconCls:'icon-add',
				handler:function(){
					//弹出采购申请窗口
					$('#addOrdersDlg').dialog('open');
				}
			});	
			break;
		case 'doCheck': url="orders_listByPage.action?t1.state=0&";break;
		case 'doStart': url="orders_listByPage.action?t1.state=1&";break;
		case 'doInStore': url="orders_listByPage.action?t1.state=2&";break;
		case 'doOutStore': url="orders_listByPage.action?t1.state=0&";break;
	}
	url+='t1.type=' + type;
	
		
	
	//设置订单列表的url;
	datagridCfg.url = url;
	$('#grid').datagrid(datagridCfg);
}

function initOrdersDlg(){
	//订单详情窗口设置
	var ordersDlgCfg = {
			title:'订单详情',
			width:700,
			height:320,
			modal:true,
			closed:true
		};
	
	//订单详情窗口的工具栏
	var ordersDlgToolbar = [];//new Array();
	//导出
	ordersDlgToolbar.push({
		text:'导出',
		iconCls:'icon-add',
		handler:function(){
			//下载
			$.download('orders_exportById.action',{id:$('#uuid').html()});
		}
	});
	//物流详情
	ordersDlgToolbar.push({
		text:'物流详情',
		iconCls:'icon-add',
		handler:function(){
			//运单号
			var waybillsn = $('#waybillsn').html();
			
			if(!waybillsn){
				$.messager.alert('提示', "没有物流信息", 'info');
				return;
			}
			//弹出物流详情窗口
			$('#waybillDetailDlg').dialog({
				title:'物流详情',
				width:500,
				height:300,
				modal:true
			});
			//物流明细
			$('#waybilldetailGrid').datagrid({
				url:'orders_waybilldetailList.action?waybillsn=' +waybillsn,
				columns:[[
			          {field:'exedate',title:'执行日期',width:100},
			  		  {field:'exetime',title:'执行时间',width:100},
			  		  {field:'info',title:'执行信息',width:100}
				]],
				singleSelect:true
			});
		}
	});
	if(oper == 'doCheck'){
		//push往数组添加元素
		ordersDlgToolbar.push({
				text:'审核',
				iconCls:'icon-add',
				handler:doCheck//按钮的点击事件时调用的方法
			}
		);
	}
	if(oper == 'doStart'){
		//push往数组添加元素
		ordersDlgToolbar.push({
				text:'确认',
				iconCls:'icon-add',
				handler:doStart//按钮的点击事件时调用的方法
			}
		);
	}
	
	
	//如果工具栏有按钮，加入订单详情窗口设置
	if(ordersDlgToolbar.length > 0){
		ordersDlgCfg.toolbar=ordersDlgToolbar;
		//ordersDlgCfg['toolbar']=ordersDlgToolbar
	}
	
	//订单详情窗口
	$('#ordersDlg').dialog(ordersDlgCfg);
}


function initOrderdetailGrid(){
	//订单明细表格的配置
	var itemgridCfg = {
			columns : [ [ 
			             {field:'uuid',title:'编号',width:60},
				  		 {field:'goodsuuid',title:'商品编号',width:100},
				  		 {field:'goodsname',title:'商品名称',width:100},
				  		 {field:'price',title:'价格',width:100},
				  		 {field:'num',title:'数量',width:100},
				  		 {field:'money',title:'金额',width:100},
				  		 {field:'state',title:'状态',width:100,formatter:formatDetailState}
			] ],
			singleSelect : true
		};
	//入库
	if(oper == 'doInStore' || oper == "doOutStore"){
		itemgridCfg.onDblClickRow = function(rowIndex, rowData){
			//打开入库窗口
			$('#itemDlg').dialog('open');
			
			$('#id').val(rowData.uuid);//明细编号
			$('#goodsname').html(rowData.goodsname);
			$('#goodsuuid').html(rowData.goodsuuid);
			$('#num').html(rowData.num);
		}
	}
	
	//订单明细表格
	$('#itemgrid').datagrid(itemgridCfg);
}

function initTitle(){
	var title = "";
	switch(oper){
		case 'orders': 
			if(type == 1){
				title="采购订单查询";
			}
			if(type == 2){
				title="销售订单查询";
			}
			break;
		case 'myorders': 
			if(type == 1){
				title="我的采购订单";
			}
			if(type == 2){
				title="我的销售订单";
			};
			break;
		case 'doCheck': title="采购审核";break;
		case 'doStart': title="采购确认";break;
		case 'doInStore': title="采购入库";break;
		case 'doOutStore': title="销售出库";break;
	}
	document.title=title;
}