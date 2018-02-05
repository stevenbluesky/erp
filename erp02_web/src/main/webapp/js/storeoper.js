$(function(){
	$('#grid').datagrid({
		url : 'storeoper_listByPage.action',
		columns : [[
		  		    {field:'uuid',title:'编号',width:100},
		  		    {field:'empName',title:'操作员工',width:100},
		  		    {field:'opertime',title:'操作日期',width:100,formatter:function(value){
		  		    	return new Date(value).Format('yyyy-MM-dd');
		  		    }},
		  		    {field:'storeName',title:'仓库编号',width:100},
		  		    {field:'goodsName',title:'商品编号',width:100},
		  		    {field:'num',title:'数量',width:100},
		  		    {field:'type',title:'类型',width:100,formatter:function(value){
		  		    	if(value * 1 == 1){
		  		    		return "入库";
		  		    	}
		  		    	if(value * 1 == 2 ){
		  		    		return "出库";
		  		    	}
		  		    }}
		]],
		singleSelect : true,
		pagination:true
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		/*if(formData['t2.opertime'] != ''){
			formData['t2.opertime'] += " 23:59:59";
		}*/
		$('#grid').datagrid('load',formData);
	});
});