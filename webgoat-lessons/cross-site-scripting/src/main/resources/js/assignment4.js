function ace_collect2() {
    let code = "";
    $(".ace_line").each(function(i, el) {
        code += el.innerHTML;
    });
    console.log(code);
    code = $(".ace_content")[1].innerHTML;
    $.ajax({
        type: "POST",
        url: "/WebGoat/CrossSiteScripting/attack4",
        dataType: "text",
        data: {
            editor: code
        }
    });
}