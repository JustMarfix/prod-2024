<template>
  <div
    v-if="isShown"
    @click="closeModal"
    class="top-0 left-0 h-screen w-screen bg-gray-900 fixed opacity-20 z-40"
  ></div>
  <div
    v-if="isShown"
    class="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 z-50"
  >
    <div class="relative p-4 w-full max-w-md max-h-full top-0 right-0 left-0 z-50">
      <div class="relative bg-white rounded-lg shadow dark:bg-gray-700 min-w-80">
        <div
          class="flex items-center justify-between p-4 md:p-5 border-b rounded-t dark:border-gray-600"
        >
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Добавить канал</h3>
          <button
            @click="closeModal"
            type="button"
            class="text-gray-400 bg-transparent hover:bg-gray-200 hover:text-gray-900 rounded-lg text-sm w-8 h-8 ms-auto inline-flex justify-center items-center dark:hover:bg-gray-600 dark:hover:text-white"
            data-modal-toggle="crud-modal"
          >
            <svg
              class="w-3 h-3"
              aria-hidden="true"
              xmlns="http://www.w3.org/2000/svg"
              fill="none"
              viewBox="0 0 14 14"
            >
              <path
                stroke="currentColor"
                stroke-linecap="round"
                stroke-linejoin="round"
                stroke-width="2"
                d="m1 1 6 6m0 0 6 6M7 7l6-6M7 7l-6 6"
              />
            </svg>
            <span class="sr-only">Close modal</span>
          </button>
        </div>
        <!-- Modal body -->
        <form :onsubmit="(e) => e.preventDefault()" class="p-4 md:p-5">
          <div class="grid gap-4 mb-4 grid-cols-2">
            <div class="col-span-2">
              <label for="name" class="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >ID канала в телеграм</label
              >
              <input
                type="text"
                name="name"
                id="name"
                class="bg-gray-50 border border-gray-300 text-gray-900 text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-600 dark:border-gray-500 dark:placeholder-gray-400 dark:text-white dark:focus:ring-primary-500 dark:focus:border-primary-500"
                placeholder="@example"
                required
                v-model="ch_id"
              />
            </div>
            {{ text }}
          </div>
          <button
            @click="addChannel"
            type="submit"
            class="text-white inline-flex items-center bg-blue-700 hover:bg-blue-800 focus:ring-4 focus:outline-none focus:ring-blue-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-blue-600 dark:hover:bg-blue-700 dark:focus:ring-blue-800"
          >
            <svg
              class="me-1 -ms-1 w-5 h-5"
              fill="currentColor"
              viewBox="0 0 20 20"
              xmlns="http://www.w3.org/2000/svg"
            >
              <path
                fill-rule="evenodd"
                d="M10 5a1 1 0 011 1v3h3a1 1 0 110 2h-3v3a1 1 0 11-2 0v-3H6a1 1 0 110-2h3V6a1 1 0 011-1z"
                clip-rule="evenodd"
              ></path>
            </svg>
            Добавить канал
          </button>
        </form>
      </div>
    </div>
  </div>
</template>
<script>
import { defineComponent } from 'vue'
import { api } from '@/logic/api.js'
import { store } from '@/store/index.js'

export default defineComponent({
  props: {
    isShown: Boolean,
    closeModal: Function,
    id: String,
    botId: Number,
    bot_token: String
  },
  data() {
    return {
      ch_id: '',
      text: ''
    }
  },

  methods: {
    async addChannel() {
      api
        .addChannels(this.id, this.ch_id, this.botId, {bot_token: this.bot_token, chat_id:this.ch_id})
        .then(() => {
          api.getChannels(this.id).then((channels) => {
            store.data.channels = channels
          })

          this.closeModal()
          this.ch_id = ''
        })
        .catch((e) => {
          this.text = ''
          this.text = e
        })
    }
  }
})
</script>
