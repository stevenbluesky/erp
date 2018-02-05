$(function(){
	$('#grid').datagrid({
		url : 'report_orderReport.action',
		columns : [[
		            {field:'name',title:'商品类型',width:100},
		            {field:'y',title:'销售额',width:100}
		]],
		singleSelect : true,
		onLoadSuccess:function(data){
			//alert(JSON.stringify(data));
			showChart(data.rows);
		}
	});
	
	//点击查询按钮
	$('#btnSearch').bind('click',function(){
		//把表单数据转换成json对象
		var formData = $('#searchForm').serializeJSON();
		if(formData['endDate'] != ''){
			formData['endDate'] += " 23:59:59";
		}
		$('#grid').datagrid('load',formData);
	});
	 

});

function showChart(_data){
	$('#chart').highcharts({
        chart: {
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false,
            type: 'pie'
        },
        title: {
            text: '销售统计'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    format: '{point.name}: <b>{point.percentage:.1f}%</b>'
                },
                showInLegend: true
            }
        },
        series: [{
            name: "百分比",
            colorByPoint: true,
            data: _data
        }],
        credits: {
        	href: "http://www.itheima.com",
        	text: '黑马程序员'
       }
    });
}