export interface Descriptor {

  icon: string
  logo: string
  name: string
  title: string
  status: boolean
  endpoint: Array<string>
  schema: any

}

interface Metadata {
  name: string
}

interface Spec {
  displayName: string
  enabled: boolean
}

export interface Extension {
  metadata: Metadata
  spec: Spec
}

export interface PaymentExtensionItem {

  descriptor: Descriptor
  extension: Extension

}
