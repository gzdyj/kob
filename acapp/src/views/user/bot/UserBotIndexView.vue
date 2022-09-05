<template>
    <ContentFieldVue>
        <div class="game-table">
            <div>
                <span style="font-size: 130%;">我的Bot</span>
                <!--创建button按钮 触发属性data-bs-toggle target -->
                <button type="button" style="float: right;" @click="show_add_modal_handler(true)">创建Bot</button>
                <!-- Modal -->
                <div class="game-modal" id="add-bot-btn" tabindex="-1" v-if="show_add_modal">
                    <div><h5 style="margin: 2px">创建Bot</h5></div>
                    <div class="modal-body">
                        <form>
                            <div>
                                <label for="add-bot-title" class="form-label">名称</label>
                                <input style="width: 85%;" v-model="botadd.title" type="text" id="add-bot-title" placeholder="请输入Bot名称">
                            </div>

                            <div>
                                <label for="add-bot-description">简介</label>
                                <textarea style="width: 85%; margin-top: 10px;" v-model="botadd.description" id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                            </div>

                            <div>
                                <label for="add-bot-code">代码</label>
                                <VAceEditor v-model:value="botadd.content" @init="editorInit"
                                    lang="c_cpp" :options="{
                                        enableBasicAutocompletion: true,
                                        enableSnippets: true,
                                        enableLiveAutocompletion: true,
                                        fontSize: 14,
                                        tabSize: 4,
                                        showPrintMargin: false,
                                        highlightActiveLine: true,
                                    }" theme="twilight" style="height: 400px" />
                            </div>
                        </form>

                            <div class="modal-footer">
                                <div class="error_message">{{ botadd.error_message }}</div>
                                <button type="button" @click="show_add_modal_handler(false)">取消</button>
                                <button type="button" @click="add_bot">创建</button>
                            </div>

                    </div>
                </div>
                <table>
                    <thead>
                        <th>Bot名称</th>
                        <th>创建时间</th>
                        <th>操作</th>
                    </thead>
                    <tbody>
                        <tr v-for="bot in bots" :key="bot.id">
                            <td>{{ bot.title }}</td>
                            <td>{{ bot.createtime }}</td>
                            <td>
                                <button type="button" style="margin-right: 10px;" @click="show_update_modal_handler(bot.id, true)">修改</button>
                                <button type="button" @click="remove_bot(bot)">删除</button>
                                <!-- Modal -->
                                <div class="game-modal" :id="'update-bot-modal-' + bot.id" tabindex="-1" v-if="bot.show_update_modal">
                                    <div>
                                        <h5 style="margin: 2px;">修改Bot</h5>
                                    </div>  
                                    <div>
                                        <form>
                                            <div>
                                                <label for="add-bot-title" class="form-label">名称</label>
                                                <input  style="width: 85%;" v-model="bot.title" type="text" id="add-bot-title" placeholder="请输入Bot名称">
                                            </div>
                                            <div>
                                                <label for="add-bot-description">简介</label>
                                                <textarea style="width: 85%; margin-top: 10px;"  v-model="bot.description"  id="add-bot-description" rows="3"
                                                    placeholder="请输入Bot简介"></textarea>
                                            </div>
                                            <div class="mb-3">
                                                <label for="add-bot-code" class="form-label">代码</label>
                                                <VAceEditor v-model:value="bot.content"
                                                    @init="editorInit" lang="c_cpp" :options="{
                                                        enableBasicAutocompletion: true,
                                                        enableSnippets: true,
                                                        enableLiveAutocompletion: true,
                                                        fontSize: 14,
                                                        tabSize: 4,
                                                        showPrintMargin: false,
                                                        highlightActiveLine: true,
                                                    }" theme="twilight" style="height: 400px" />

                                            </div>
                                        </form>
                                    </div>
                                    <div>
                                        <div class="error_message">{{ bot.error_message }}</div>
                                        <button type="button" @click="show_update_modal_handler(bot.id, false)">取消</button>
                                        <button type="button" @click="update_bot(bot)">保存修改</button>
                                    </div>
                                </div>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    </ContentFieldVue>


</template>


<script>

import { ref, reactive } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import ContentFieldVue from '../../../components/ContentField.vue'

export default {


    components: {
    VAceEditor,
    ContentFieldVue,
},

    setup() {


        ace.config.set(
            "basePath",
            "https://cdn.jsdelivr.net/npm/ace-builds@" + require('ace-builds').version + "/src-noconflict/"
        );
        function editorInit() {
            require("ace-builds/src-noconflict/ext-language_tools");
            require("ace-builds/src-noconflict/snippets/sql");
            require("ace-builds/src-noconflict/snippets/c_cpp");
            require("ace-builds/src-noconflict/snippets/java");
            // require("ace-builds/src-noconflict/mode-sql");
            require("ace-builds/src-noconflict/mode-c_cpp");
            require("ace-builds/src-noconflict/mode-java");
            //require("ace-builds/src-noconflict/theme-monokai");
            require("ace-builds/src-noconflict/mode-html");
            require("ace-builds/src-noconflict/mode-html_elixir");
            require("ace-builds/src-noconflict/mode-html_ruby");
            require("ace-builds/src-noconflict/mode-javascript");
            require("ace-builds/src-noconflict/mode-python");
            require("ace-builds/src-noconflict/snippets/less");
            //require("ace-builds/src-noconflict/theme-chrome");
            require("ace-builds/src-noconflict/ext-static_highlight");
            require("ace-builds/src-noconflict/ext-beautify");
        }




        const store = useStore();
        let bots = ref([]);
        let show_add_modal = ref(false);



        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        });

        const refresh_bots = () => {
            $.ajax({
                url: "https://snake.zinzin.cc/api/user/bot/getlist/",
                type: "GET",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    for (const bot of resp) {
                        bot.show_update_modal = false;
                    }
                    bots.value = resp;
                }
            })
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "https://snake.zinzin.cc/api/user/bot/remove/",
                type: "POST",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                data: {
                    bot_id: bot.id,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                }
            })
        }

        const update_bot = (bot) => {
            botadd.error_message = "";
            $.ajax({
                url: "https://snake.zinzin.cc/api/user/bot/update/",
                type: "POST",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.title,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        // 点击创建bot后隐藏窗口
                        // Modal.getInstance('#update-bot-modal-' + bot.id).hide();
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                },

            })
        }


        const add_bot = () => {
            botadd.error_message = "";
            $.ajax({
                url: "https://snake.zinzin.cc/api/user/bot/add/",
                type: "POST",
                data: {
                    title: botadd.title,
                    description: botadd.title,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {

                        // 成功就清空窗口
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        botadd.error_message = "";
                        // 点击创建bot后隐藏窗口 bootstrap自带
                        // Modal.getInstance("#add-bot-btn").hide();
                        show_add_modal.value = false;
                        refresh_bots();
                    } else {
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }

        refresh_bots();
        
        const show_add_modal_handler = is_show => {
            show_add_modal.value = is_show;
        }
        const show_update_modal_handler = (bot_id, is_show) => {
            const new_bots = [];
            for (const bot of bots.value) {
                if (bot.id === bot_id) {
                    bot.show_update_modal = is_show;
                }
                new_bots.push(bot);
            }
            bots.value = new_bots;
        }

        return {
            bots,
            botadd,
            add_bot,
            remove_bot,
            update_bot,
            VAceEditor,
            editorInit,
            show_add_modal,
            show_add_modal_handler,
            show_update_modal_handler,
        }
    }

}
</script>

<style scoped>
div.game-table table {
    background-color: rgba(255,255,255,0.5);
    border-radius: 5px;
}
/* 居中 */
div.game-table {
    display: flex;
    justify-content: center;
    padding-top: 5vh;
    width: 100%;
    /* calc 计算 */
    height: calc(100% - 5vh);
}
.game_table-username {
    text-align: left;
    /* 超过长度省略号 */
    /* overflow 溢出 hidden 隐藏 ellipsis 省略 nowrap 禁止文字自动换行*/
    overflow: hidden; 
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 15vw;
}
td {
    width: 12vw;
    overflow: hidden; 
    text-overflow: ellipsis;
    white-space: nowrap;
    max-width: 12vw;
    text-align: center;
}
th {
    text-align: center;

}
div.error_message {
    color: red;
}

#codeEditBox {
    width: 100%;
    height: 200px;
}
.game-modal {
    background-color: white;
    padding: 10px;
    border-radius: 5px;
    position: absolute;
    width: 40vw;
    height: 70vh;
    left: 0;
    right: 0;
    top: 0;
    bottom: 0;
    margin: auto;
    text-align: left;

}
</style>
