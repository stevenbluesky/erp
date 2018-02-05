//提交的方法名称
var method = "";
var height=200;
var listParam="";
var saveParam="";
$(function(){
	//加载表格数据
	$('#grid').datagrid({
		url:name + '_listByPage'+listParam,
		columns:columns,
		singleSelect: true,
		pagination: true,
		pageList:[5,10,15,20,30,50],
		toolbar: [{
			text: '新增',
			iconCls: 'icon-add',
			handler: function(){
				//设置保存按钮提交的方法为add
				method = "add";
				$('#editForm').form('clear');
				//打开编辑窗口
				$('#editDlg').dialog('open');
			}
		},'-',{
			text: '导出',
			iconCls: 'icon-add',
			handler: function(){
				var submitData = $('#searchForm').serializeJSON();
				$.download(name+'_export.action?t1.type='+Request['type'],submitData);
			}
		},'-',{
			text: '导入',
			iconCls: 'icon-save',
			handler: function(){
				//打开导入窗口
				$('#importDlg').dialog('open');
			}
		}]
	});
	var importDlg = document.getElementById('importDlg');
	if(null != importDlg){
		//导入窗口
		$('#importDlg').dialog({
			title:'导入',
			width:330,
			height:106,
			closed:true,
			modal:true,
			buttons:[
			    {
			    	text:'导入',
			    	iconCls:'icon-save',
			    	handler:function(){
			    		$.ajax({
							url : 'supplier_doImport.action',//请求的url
							data : new FormData($('#importForm')[0]),//提交 的数据，表单对象
							dataType : 'json',//返回的数据类型: jquery把返回的值转成json数据
							type : 'post',//请求的方式
							processData:false,//如果要发送 DOM 树信息或其它不希望转换的信息，请设置为 false。我们发送的是表单对象, 让jquery不要处理
							contentType: false,//告诉jQuery不要设置任何内容类型文件头，服务端读取时按字节流读取
							success : function(rtn) {//请求成功时的回调函数, rtn就服务端响应回来的数据
								$.messager.alert('提示', rtn.message, 'info',function() {
									if(rtn.success){
										//关闭导入的窗口
										$('#importDlg').dialog('close');
										//刷新表格
										$('#grid').datagrid('reload');
									}
								});
							}
						});
			    	}
			    }
			]
		});
	}
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		$('#grid').datagrid('load',formData);
	});
	$('#btnSave').bind('click',function(){
		//把表单数据转换成json对象
		var isValid = $('#editForm').form('validate');
		if(isValid==false){
			return;
		}
		var formData = $('#editForm').serializeJSON();
		$.ajax({
			url:name+'_'+method+saveParam,
			data:formData,
			dataType:'json',
		})
	});
	//初始化编辑窗口
	$('#editDlg').dialog({
		title: '编辑',//窗口标题
		width: 300,//窗口宽度
		height: height,//窗口高度
		closed: true,//窗口是是否为关闭状态, true：表示关闭
		modal: true,//模式窗口
		buttons:[{
			text:'保存',
			iconCls: 'icon-save',
			handler:function(){
				//用记输入的部门信息
				var submitData= $('#editForm').serializeJSON();
				$.ajax({
					url: name + '_' + method+".action"+saveParam,
					data: submitData,
					dataType: 'json',
					type: 'post',
					success:function(rtn){
						//{success:true, message: 操作失败}
						$.messager.alert('提示',rtn.message, 'info',function(){
							if(rtn.success){
								//关闭弹出的窗口
								$('#editDlg').dialog('close');
								//刷新表格
								$('#grid').datagrid('reload');
							}
						});
					}
				});
			}
		},{
			text:'关闭',
			iconCls:'icon-cancel',
			handler:function(){
				//关闭弹出的窗口
				$('#editDlg').dialog('close');
			}
		}]
	});

});


/**
 * 删除
 */
function del(uuid){
	$.messager.confirm("确认","确认要删除吗？",function(yes){
		if(yes){
			$.ajax({
				url: name + '_delete?id=' + uuid,
				dataType: 'json',
				type: 'post',
				success:function(rtn){
					$.messager.alert("提示",rtn.message,'info',function(){
						//刷新表格数据
						$('#grid').datagrid('reload');
					});
				}
			});
		}
	});
}

/**
 * 修改
 */
function edit(uuid){
	//弹出窗口
	$('#editDlg').dialog('open');

	//清空表单内容
	$('#editForm').form('clear');

	//设置保存按钮提交的方法为update
	method = "update";

	//加载数据
	$('#editForm').form('load',name + '_get?id=' + uuid);
}