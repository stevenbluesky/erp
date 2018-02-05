$(function(){
	var date = new Date();
	var year = date.getFullYear();
	$('#year').combobox('setValue',year);
	$('#grid').datagrid({
		url : 'report_trendReport.action',
		columns : [[
		            {field:'name',title:'月份',width:100},
		            {field:'y',title:'销售额',width:100}
		]],
		singleSelect : true,
		queryParams:{year:year},
		onLoadSuccess:function(data){
			//alert(JSON.stringify(data));
			showChart(data.rows);
		}
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		
		$('#grid').datagrid('load',formData);
	});
	 

});

function showChart(_data){
	var months=[];
	for(var i=1;i<=12;i++){
		months.push(i+"月");
	}
	$('#chart').highcharts({
        chart: {
        	 type: 'line'
        },
        title: {
            text: $('#year').combobox('getValue') + '年度销售趋势'
        },
        subtitle: {
            text: 'Source: itheima.com'
        },
        xAxis: {
            categories: months
        },
        yAxis: {
            title: {
                text: 'RMB(￥)'
            }
        },
        plotOptions: {
            line: {
                dataLabels: {
                    enabled: true
                },
                enableMouseTracking: false
            }
        },
        series: [{
            name: '销售趋势',
            data: _data
        }]
    });
}