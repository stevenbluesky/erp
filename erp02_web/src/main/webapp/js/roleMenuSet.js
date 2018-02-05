$(function(){
	
	/*$('#tree').tree({
		checkbox:true,//定义是否在每一个借点之前都显示复选框。
		animate:true,//节点在展开或折叠的时候是否显示动画效果
		data: [{
			text: 'Item1',
			children: [{
				text: 'Item11'
			},{
				text: '部门',
				checked:true//表示该节点是否被选中
			}]
		},{
			text: 'Item2'
		}]
	});*/
	
	//角色列表
	$('#grid').datagrid({
		url : 'role_list.action',
		columns : [ [ 
			{field:'uuid',title:'编号',width:100},
			{field:'name',title:'名称',width:100}
		] ],
		singleSelect : true,
		onClickRow:function(rowIndex, rowData){
			var roleuuid = rowData.uuid;//角色的编号
			//菜单权限树
			$('#tree').tree({
				checkbox:true,
				animate:true,
				url:'role_readRoleMenus.action?id=' + roleuuid//从远程url加载数据

			});
		}
	});

	
	
	//保存
	$('#btnSave').bind('click',function(){
		//获取所有选中的节点
		var nodes = $('#tree').tree('getChecked');
		var ids = [];//菜单编号的数组
		$.each(nodes,function(i,node){
			ids.push(node.id);//加上菜单编号
		});
		//提交的数据
		var submitData = {};
		//把菜单编号传给提交的数据
		submitData.ids = ids.toString();//菜单编号以逗号分割连接起来了
		
		//获取选中的角色
		var row = $('#grid').datagrid('getSelected');//返回第一个被选中的行或如果没有选中的行则返回null
		if(row == null){
			$.messager.alert('提示', "请选角色!", 'info');
			return;
		}
		//把角色编号传给提交的数据
		submitData.id=row.uuid;
		//提交
		$.ajax({
			url : 'role_updateRoleMenus.action',//请求的url
			data : submitData,//查询条件数据，请求的数据
			dataType : 'json',//返回的数据类型: jquery把返回的值转成json数据
			type : 'post',//请求的方式
			success : function(rtn) {//请求成功时的回调函数, rtn就服务端响应回来的数据
				$.messager.alert('提示', rtn.message, 'info');
			}
		});
		
	});
});