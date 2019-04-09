/**
 * 校外客户信息列表
 */

//设置全局表单提交格式
Vue.http.options.emulateJSON = true;

// Vue实例
var vm = new Vue({
    el: '#app',
    data() {
        return {
            //element-ui的table需要的参数必须是Array类型的
            goods: [{
                id: '',
                title: '',
                price: '',
                image: '',
                category: '',
                brand: '',
                seller: ''
            }],
            odds: [
            ],
            createTime: this.getCreateTime(),

            //编辑表
            editor: {
                title: '',
                price: '',
                image: '',
                category: '',
                brand: '',
                seller: ''
            },
            winCount:0,
            lostCount:0,
            //添加dialog
            showSave: false,
            //编辑dialog
            showEditor: false,

            //条件查询单独封装的对象
            searchEntity: {},

            //checkbox选择的行中所有数据，将会放入multipartSelection数组中
            multipleSelection: [],
            selectIds: [], //被checkbox选择的id值，用于批量删除
            count: 0, //tag栏，此项那是checkbox选择了几行

            //分页选项
            pageConf: {
                //设置一些初始值(会被覆盖)
                pageCode: 1, //当前页
                pageSize: 6, //每页显示的记录数
                totalPage: 12, //总记录数
                pageOption: [6, 10, 20], //分页选项
            },

            loading: {},

            //文件上传的参数
            dialogImageUrl: '',
            dialogVisible: false,
            //图片列表（用于回显图片）
            fileList: [{name: '', url: ''}],

            activeIndex: '2', //默认激活
        }
    },
    methods: {
        /**
         * loading加载动画
         */
        loadings() {
            this.loading = this.$loading({
                lock: true,
                text: '拼命加载中',
                spinner: 'el-icon-loading',
            });
            setTimeout(() => {
                this.loading.close();
            }, 20000000);
        },

        /**
         * Public method
         */
        //刷新列表
        reloadList() {
            console.log("totalPage:" + this.pageConf.totalPage + ",pageSize:" + this.pageConf.pageSize + ",:pageCode:" + this.pageConf.pageCode);
            this.search(this.pageConf.pageCode, this.pageConf.pageSize);
        },
        //条件查询
        search(pageCode, pageSize) {
            this.loadings();
            this.$http.post('/goods/findByConPage.do?pageSize=' + pageSize + '&pageCode=' + pageCode, this.searchEntity).then(result => {
                console.log(result);
                this.goods = result.body.rows;
                this.pageConf.totalPage = result.body.total;
                this.loading.close(); //数据更新成功就手动关闭动画
            });

        },
        //条件查询
        insertLastYz(inputDateStr) {
            this.loadings();
            this.$http.get('odds/insertLastYz.do?inputDateStr=' + inputDateStr, this.searchEntity).then(result => {
            this.loading.close(); //数据更新成功就手动关闭动画
        });

        },
        // 获取所有数据
        findAll() {
            this.loadings();
            this.createTime = this.getCreateTime();
            this.$http.get('odds/getLastYz.do').then(result => {
                this.odds = result.body;
                this.loading.close(); //数据更新成功就手动关闭动画

        });

        },
        // 获取已完成数据
        findFinish() {
            this.loadings();
            this.createTime = this.getCreateTime();
            this.$http.get('odds/getLastYzFinish.do').then(result => {
                this.odds = result.body;
            this.loading.close(); //数据更新成功就手动关闭动画

        });

        },




        // 获取所有数据
        getCreateTime() {
            var date = new Date();
            var y = date.getFullYear();
            var MM = date.getMonth() + 1;
            MM = MM < 10 ? ('0' + MM) : MM;
            var d = date.getDate();
            d = d < 10 ? ('0' + d) : d;
            var h = date.getHours();
            h = h < 10 ? ('0' + h) : h;
            var m = date.getMinutes();
            m = m < 10 ? ('0' + m) : m;
            var s = date.getSeconds();
            s = s < 10 ? ('0' + s) : s;
            return y + '-' + MM + '-' + d + ' ' + h + ':' + m + ':' + s;
        },//刷新列表
        winAdd() {
            this.winCount = this.winCount + 1
        },
        winDec() {
            this.winCount = this.winCount - 1
        },
        lostAdd() {
            this.lostCount = this.lostCount + 1
        },
        lostDec() {
            this.lostCount = this.lostCount - 1
        }

    },

    // 生命周期函数
    created() {
        this.findAll();
        // this.search(this.pageConf.pageCode, this.pageConf.pageSize);
        this.loadings(); //加载动画
    },

});
