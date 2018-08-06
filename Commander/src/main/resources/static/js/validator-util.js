/**
 * Created by slw on 2018/6/29.
 */
$(function () {
    $.fn.validator.Constructor.VALIDATORS.maxbytelength = function($el) {
        var maxbytelength = $el.attr('data-maxbytelength');
        return GetByteLength($el.val()) > maxbytelength;
    }

    var GetByteLength = function (val) {
        var byteLength = 0;
        for (var i = 0; i < val.length; i++) {
            if (val[i].match(/[^\x00-\xff]/ig) != null) {
                byteLength += 2;
            } else {
                byteLength += 1;
            }
        }
        return byteLength;
    }
});