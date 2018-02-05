//当前编辑行的下标
var existEditIndex=-1;
$(function(){
	//明细表格
	$('#ordersgrid').datagrid({
		columns : [ [ 
			{field:'goodsuuid',title:'商品编号',width:100,editor:{type:'numberbox',options:{disabled:true}}},
			{field:'goodsname',title:'商品名称',width:100,editor:{
				type:'combobox',
				options:{
					url:'goods_list.action',
					textField:'name',//显示的文本
					valueField:'name',//保存明细数据时，需要保存商品的名称，不用uuid的原因，已经有一列为商品编号
					onSelect:function(record){
						//当选中一项数据时触发
						//alert(JSON.stringify(record));//record选中的商品信息
						var price = record.inprice;//采购价格
						if(type==2){
							price = record.outprice;
						}
						var goodsuuid = record.uuid;//商品编号
						
						//获取价格的编辑器
						var priceEditor = $('#ordersgrid').datagrid('getEditor',{
							index:existEditIndex,
							field:'price'
						});
						//priceEditor.target 代表编辑器的输入框(input)
						//给编辑的输入框 赋值
						$(priceEditor.target).val(price);
						//alert(JSON.stringify(priceEditor.target));
						
						//获取商品编号的编辑器
						var goodsuuidEditor = $('#ordersgrid').datagrid('getEditor',{
							index:existEditIndex,
							field:'goodsuuid'
						});
						$(goodsuuidEditor.target).val(goodsuuid);
						
						//计算金额
						cal();
						//计算 合计金额
						sum();
					}
				}				
			}},
			{field:'price',title:'价格',width:100,editor:{type:'numberbox',options:{disabled:true,precision:2}}},
			{field:'num',title:'数量',width:100,editor:'numberbox'},
			{field:'money',title:'金额',width:100,editor:{type:'numberbox',options:{disabled:true,precision:2}}},
			{field:'-',title:'操作',width:100,formatter:function(value,row,rowIndex){
				if(row.num == '合计'){
					return;
				}
				return '<a href="javascript:void(0);" onclick="delRow(' + rowIndex + ')">删除</a>';
			}}
		] ],
		singleSelect : true,
		showFooter:true,//显示行脚
		toolbar: [ {
			text : '新增',
			iconCls : 'icon-add',
			handler : function() {
				var row = {num:0,money:0};
				
				//以前加过行，并编辑过
				if(existEditIndex > -1){
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}
				
				//往最后追加一行
				$('#ordersgrid').datagrid('appendRow',row);
				//获取总记录
				var rows = $('#ordersgrid').datagrid('getRows');
				//最后一行的下标
				existEditIndex = rows.length - 1;				
				//开启最后一行编辑
				$('#ordersgrid').datagrid('beginEdit',existEditIndex);
				
				//绑定自动计算的事件
				bindGridEvent();
			}
		},'-',{
			text:'提交',
			iconCls:'icon-save',
			handler:function(){
				//关闭编辑的行
				if(existEditIndex > -1){
					$('#ordersgrid').datagrid('endEdit',existEditIndex);
				}
				var submitData = $('#orderForm').serializeJSON();//{},java map
				if(submitData['t.supplieruuid'] == ''){
					$.messager.alert('提示', "请选择供应商", 'info');
					return;
				}
				//获取所有的明细的行
				var rows = $('#ordersgrid').datagrid('getRows');
				//明细转成json格式的字符串
				submitData.jsonString = JSON.stringify(rows);
				//订单类型
				submitData['t.type']=type;
				$.ajax({
					url : 'returnorders_add.action',//请求的url
					data : submitData,//查询条件数据，请求的数据{jsonString: JSON.stringify(rows)}
					dataType : 'json',//返回的数据类型: jquery把返回的值转成json数据
					type : 'post',//请求的方式
					success : function(rtn) {//请求成功时的回调函数, rtn就服务端响应回来的数据
						$.messager.alert('提示', rtn.message, 'info', function() {
							if(rtn.success){
								//清空供应商
								$('#supplier').combogrid('clear');
								//清空表格数据
								$('#ordersgrid').datagrid('loadData',{total:0,rows:[],footer:[{num:'合计',money:0}]});
								//关闭新增窗口
								$('#addOrdersDlg').dialog('close');
								//刷新订单列表
								$('#grid').datagrid('reload');
							}
						});
					}
				});
			}
		} ],
		onClickRow:function(rowIndex, rowData){
			//用户单击一行的时候触发
			//rowIndex：点击的行的索引值，该索引值从0开始。
			//rowData：对应于点击行的记录
			
			//当前是否存在编辑的行
			if(existEditIndex > -1){
				//index = rows.length - 1;
				$('#ordersgrid').datagrid('endEdit',existEditIndex);
			}
			//开启另外一行的编辑
			$('#ordersgrid').datagrid('beginEdit',rowIndex);
			
			//existEditIndex 永远保存的是当前编辑的行
			existEditIndex=rowIndex;
			//绑定自动计算的事件
			bindGridEvent();
		}
	});
	//加载行的数据，构建合计这一行
	$('#ordersgrid').datagrid('reloadFooter',[{num: '合计', money: 0}]);
	
	//供应下拉表格
	$('#supplier').combogrid({    
	    panelWidth:750,//面板宽度
	    //value:'006',//选中的值
	     
	    idField:'uuid',//提交的数据,等同valueField
	    textField:'name',    
	    url:'supplier_list.action?t1.type='+type,//请求数据,t1.type=1，只查询供应商类型
	    columns:[[    
	         {field:'uuid',title:'编号',width:100},
	  		 {field:'name',title:'名称',width:100},
	  		 {field:'address',title:'联系地址',width:100},
	  		 {field:'contact',title:'联系人',width:100},
	  		 {field:'tele',title:'联系电话',width:100},
	  		 {field:'email',title:'邮件地址',width:100}
	    ]]    
	}); 

});

/**
 * 计算金额
 */
function cal(){
	
	//获取数量, 获取编辑的输入框的值，
	var numEditor = getEditor('num');
	var num = $(numEditor.target).val() * 1;
	
	//获取价格
	var priceEditor = getEditor('price');
	var price = $(priceEditor.target).val() * 1;
	
	//数量*价格
	var money = num * price;
	
	//获取金额的编辑器
	var moneyEditor = getEditor('money');
	//toFixed(2) 保留2位小数, 返回的是字符串
	$(moneyEditor.target).val(money.toFixed(2));
	//获取所有的行
	var rows = $('#ordersgrid').datagrid('getRows');
	//当前编辑行的记录,从datagrid中来
	var row = rows[existEditIndex];
	//把金额放到datagrid中的这条记录里面
	row.money = money.toFixed(2);
}

/**
 * 计算合计金额
 */
function sum(){
	//所有的行
	var rows = $('#ordersgrid').datagrid('getRows');
	//总金额
	var total = 0;
	//累计总金额
	$.each(rows, function(i, row){
		total += row.money * 1;
	});
	//加载行的数据，构建合计这一行
	$('#ordersgrid').datagrid('reloadFooter',[{num: '合计', money: total.toFixed(2)}]);
}

/**
 * 获取编辑器
 * @param _field
 * @returns
 */
function getEditor(_field){
	return $('#ordersgrid').datagrid('getEditor',{
		index:existEditIndex,
		field:_field
	})
}

/**
 * 绑定事件
 */
function bindGridEvent(){
	//获取数量编辑器
	var numEditor = getEditor('num');
	//数量的输入框
	$(numEditor.target).bind('keyup',function(){
		//计算金额
		cal();
		//合计金额
		sum();
	});
}

/**
 * 删除行
 * @param index
 */
function delRow(index){
	//判断是否存在编辑的行，有就关闭。让它的数据保存到datagrid中。内存对象
	if(existEditIndex > -1){
		$('#ordersgrid').datagrid('endEdit',existEditIndex);
	}
	
	$('#ordersgrid').datagrid('deleteRow',index);
	sum();
	//获取数据
	var data = $('#ordersgrid').datagrid('getData');
	//加载本地数据，旧的行将被移除。删除已经加载好的数据delRow(下标：越界)，重新加载数据的时候，会重新分配delRow(下标)
	$('#ordersgrid').datagrid('loadData',data);
	
	//没有正在编辑的行
	existEditIndex=-1;
	
}