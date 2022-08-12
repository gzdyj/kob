
export default {
    state: {
        status: "matching", //matching表示正在匹配界面，playing表示对战页面
        socket: null,
        // opponent 对手
        opponent_username: "",
        opponent_photo: "",
        gamemap: null,

    },
    getters: {

    },
    //mutaions 变化
    mutations: {
        updateSocket(state, socket) {
            state.socket = socket;
        },
        updateOpponent(state, opponent) {
            state.opponent_username = opponent.username;
            state.opponent_photo = opponent.photo;
        },
        updateStatus(state, status) {
            state.status = status;
        },
        updateGamemap(state, gamemap) {
            state.gamemap = gamemap;
        }
    },
    actions: {
    },
    modules: {

    }
}
