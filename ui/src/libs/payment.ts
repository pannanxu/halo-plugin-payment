type ChannelProfile = {
  name: string
  displayName: string
  icon: string
}

class Payment {
  channels: ChannelProfile[] = [];

  constructor() {
    this.channels = [
      {
        name: "paypal",
        displayName: "PayPal",
        icon: "https://www.paypalobjects.com/webstatic/en_US/i/buttons/checkout-logo-large.png"
      },
      {
        name: "stripe",
        displayName: "Stripe",
        icon: "https://stripe.com/img/v3/home/logos/stripe-logo-mark.png"
      },
      {
        name: "braintree",
        displayName: "Braintree",
        icon: "https://www.braintreepayments.com/images/paypal_logo.png"
      },
      {
        name: "square",
        displayName: "Square",
        icon: "https://squareup.com/static/images/logos/square-logo-black.png"
      },
      {
        name: "payoneer",
        displayName: "Payoneer",
        icon: "https://www.payoneer.com/wp-content/uploads/2016/05/payoneer-logo.png"
      },
      {
        name: "payu",
        displayName: "PayU",
        icon: "https://www.payu.com/wp-content/uploads/2016/05/payu-logo.png"
      },

    ];
  }

  public getChannels() {
    return this.channels;
  }

  public registerChannel(channel: ChannelProfile) {
    this.unregisterChannel(channel);
    this.channels.push(channel);
  }

  public unregisterChannel(channel: ChannelProfile) {
    this.channels = this.channels.filter(c => c.name !== channel.name);
  }
}

(window as any).payment = new Payment();
