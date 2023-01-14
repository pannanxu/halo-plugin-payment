<script lang="ts" setup>
import {VButton, VPageHeader, VSwitch,} from "@halo-dev/components";

import apiClient from "@/utils/api-client";
import {ref} from "vue";
import type {PaymentExtensionItem} from "@/index.types";

const payments = ref<PaymentExtensionItem[]>([] as PaymentExtensionItem[]);

const handleLoadPayments = async () => {
  const {data} = await apiClient.get<PaymentExtensionItem[]>('/apis/payment.mvvm.io/v1/list/all')
  payments.value = data;
}


const handleOnChange = async (payment: PaymentExtensionItem) => {
  payment.extension.spec.enabled = !payment.extension.spec.enabled
  try {
    if (payment.extension.spec.enabled) {
      const {data} = await apiClient.get<PaymentExtensionItem[]>(`/apis/payment.mvvm.io/v1/enable/${payment.extension.metadata.name}`)
      console.log('enable: ', data);
    } else {
      const {data} = await apiClient.get<PaymentExtensionItem[]>(`/apis/payment.mvvm.io/v1/disable/${payment.extension.metadata.name}`)
      console.log('disable: ', data); 
    }
  } catch (e) {
    console.error(e)
  } finally {
    await handleLoadPayments()
  }
}

const handleInitPaymentConfig = async (payment: PaymentExtensionItem) => {
  try {
    const {data} = await apiClient.get<PaymentExtensionItem[]>(`/apis/payment.mvvm.io/v1/init/${payment.descriptor.name}`)
    console.log('init: ', data);
  } catch (e) {
    console.error(e)
  } finally {
    await handleLoadPayments()
  }
}

handleLoadPayments()
</script>

<template>
  <BasicLayout>
    <VPageHeader title="设置">
      <template>
        <IconSettings class="mr-2 self-center"/>
      </template>
    </VPageHeader>

    <div class="m-0 md:m-4">
      <div class="bg-white">
        <Transition mode="out-in" name="fade">
          <div class="bg-white p-4">
            <VButton @click="handleLoadPayments"> 刷新</VButton>
            <ul id="payment">
              <li id="payment-wechat" class="flex" v-for="payment in payments">
                <div>
                  {{ payment.descriptor.title }}
                </div>
                <div>
                  <VSwitch
                    :model-value="payment.extension.spec.enabled"
                    @click="handleOnChange(payment)"
                  />
                </div>
                <div>
                  <VButton
                    @click="handleInitPaymentConfig(payment)"
                    block
                    type="secondary"
                  >
                    刷新配置{{payment.descriptor.status}}
                  </VButton>
                </div>
              </li>
            </ul>
          </div>
        </Transition>
      </div>
    </div>
  </BasicLayout>
</template>

<style scoped>
.title {
  color: red;
}
</style>
