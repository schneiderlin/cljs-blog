// import { Socket } from "phoenix"
const { Socket } = require('phoenix')

let socket = new Socket("/socket", {params: {userToken: "123"}})
socket.connect()