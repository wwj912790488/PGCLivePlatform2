var dateLocale = {
    "direction": "ltr",
    "format": "YYYY/MM/DD HH:mm",
    "separator": " - ",
    "applyLabel": "应用",
    "cancelLabel": "清除",
    "fromLabel": "从",
    "toLabel": "至",
    "customRangeLabel": "自定义",
    "daysOfWeek": [
        "日",
        "一",
        "二",
        "三",
        "四",
        "五",
        "六"],
    "monthNames": [
        "1月",
        "2月",
        "3月",
        "4月",
        "5月",
        "6月",
        "7月",
        "8月",
        "9月",
        "10月",
        "11月",
        "12月"
    ],
    "firstDay": 0
};

var handleFutureDatePicker = function () {
    var dateRanges = {
        '今天': [moment(), moment()],
        '明天': [moment().add(1, 'days'), moment().add(1, 'days')],
        '后续7天': [moment(), moment().add(6, 'days')],
        '后续30天': [moment(), moment().add(29, 'days')],
        '本月': [moment().startOf('month'), moment().endOf('month')],
        '下月': [moment().add(1, 'month').startOf('month'), moment().add(1, 'month').endOf('month')]
    };

    function cb(start, end) {
        $('#filterFromToDate span').html(start.format('YYYY-MM-DD') + ' - ' + end.format('YYYY-MM-DD'));
        $('input[name="filterFromToDate"]').val($('#filterFromToDate span').html());
    }

    $('#filterFromToDate').daterangepicker({
        locale: dateLocale,
        timePicker: false,
        startDate: moment(),
        endDate: moment().add(29, 'days'),
        ranges: dateRanges
    }, cb);

    $('#filterFromToDate').on('cancel.daterangepicker', function (ev, picker) {
        $('#filterFromToDate span').html('');
    });

    $('#filterFromToDate').on('apply.daterangepicker', function (ev, picker) {
        cb(picker.startDate, picker.endDate);
    });
};

var handleLastDatePicker = function () {
    var dateRanges = {
        '今天': [moment(), moment()],
        '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
        '最近7天': [moment().subtract(6, 'days'), moment()],
        '最近30天': [moment().subtract(29, 'days'), moment()],
        '本月': [moment().startOf('month'), moment().endOf('month')],
        '上月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')]
    };

    function cb(start, end) {
        $('#filterFromToDate span').html(start.format('YYYY-MM-DD') + ' - ' + end.format('YYYY-MM-DD'));
        $('input[name="filterFromToDate"]').val($('#filterFromToDate span').html());
    }

    $('#filterFromToDate').daterangepicker({
        locale: dateLocale,
        timePicker: false,
        startDate: moment(),
        endDate: moment().subtract(29, 'days'),
        ranges: dateRanges
    }, cb);

    $('#filterFromToDate').on('cancel.daterangepicker', function (ev, picker) {
        $('#filterFromToDate span').html('');
    });

    $('#filterFromToDate').on('apply.daterangepicker', function (ev, picker) {
        cb(picker.startDate, picker.endDate);
    });

    function exportDateCallback(start, end) {
        $('#exportFromToDate span').html(start.format('YYYY-MM-DD') + ' - ' + end.format('YYYY-MM-DD'));
    }

    $('#exportFromToDate').daterangepicker({
        locale: dateLocale,
        timePicker: false,
        startDate: moment(),
        endDate: moment().subtract(29, 'days'),
        ranges: dateRanges
    }, exportDateCallback);

    $('#exportFromToDate').on('cancel.daterangepicker', function (ev, picker) {
        $('#exportFromToDate span').html('');
    });

    $('#exportFromToDate').on('apply.daterangepicker', function (ev, picker) {
        exportDateCallback(picker.startDate, picker.endDate);
    });
};
