$(function(){
    $('#grid').datagrid({
        title: '员工信息列表',
        url:'emp_listByPage.action',
        columns:[[
                    {checkbox:true},
                    {field:'uuid',title:'编号',width:100},
                    {field:'username',title:'系统登陆名称',width:100},
                    {field:'name',title:'真实姓名',width:100},
                    {field:'gender',title:'性别',width:100,formatter:function(gender){
                        switch(gender){
                            case 0: return '女';
                            case 1: return '男';
                            default: return '未知';
                        }
                    }},
                    {field:'email',title:'电子邮箱地址',width:100},
                    {field:'tele',title:'联系电话',width:100},
                    {field:'address',title:'联系地址',width:100},
                    {field:'birthday',title:'出生年月日',width:100,formatter:function(birthday){
                        if(birthday){
                            return new Date(birthday).Format("yyyy-MM-dd");
                        }
                        return "";
                    }},
                    {field:'dep',title:'部门',width:100,formatter:function(dep){
                        if(dep){
                            return dep.name;
                        }
                        return "";
                    }},
                    {field:'-',title:'操作',width:200,formatter:function(value,row,index){
                          var operation = '<a href="javascript:void(0)" onclick="updatePwd_reset(' + row.uuid + ')">重置密码</a> ';
                          return operation;
                    }}
        ]],
        singleSelect: true,
        pagination: true,
        rownumbers: true
});

    $('#editDlg').dialog({
    	title:"重置员工密码",
    	width:260,
    	height:120,
    	iconCls:'icon-save',
    	modal:true,
    	closed:true,
    	buttons:[
    	    {
    	    	text:'保存',
    	    	iconCls: 'icon-save',
    	    	handler:function(){
    	            var datas = $('#editForm').serializeJSON();
    	            $.ajax({
    	                url: 'emp_updatePwd_reset.action',
    	                dataType:'json',
    	                data: datas,
    	                type: 'post',
    	                success: function(rtn){
    	                	$.messager.alert("提示", rtn.message, 'info', function(){
    	                		if(rtn.success){
		                            //关闭窗口
		                            $('#editDlg').dialog('close');
		                            //刷新列表
		                            $('#grid').datagrid('reload');
    	                		}
	                   });
    	                }
    	            });
    	    	}
    	    }
    	]
    });
});

function updatePwd_reset(uuid){
    //清除表单内容，防止数据混乱
    $('#editForm').form('load',{id:uuid,newPwd:""});
    //弹出窗口
    $('#editDlg').dialog('open');
}